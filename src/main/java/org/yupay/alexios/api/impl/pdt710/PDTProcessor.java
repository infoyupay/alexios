/*
 * Copyright (C) 2021 David Vidal Escudero - Free Open Source Software Consultancy
 *
 *       This program is free software: you can redistribute it and/or modify
 *       it under the terms of the GNU General Public License as published by
 *       the Free Software Foundation, either version 3 of the License, or
 *       (at your option) any later version.
 *
 *       This program is distributed in the hope that it will be useful,
 *       but WITHOUT ANY WARRANTY; without even the implied warranty of
 *       MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *       GNU General Public License for more details.
 *
 *       You should have received a copy of the GNU General Public License
 *       along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.yupay.alexios.api.impl.pdt710;

import com.google.api.services.sheets.v4.model.RowData;
import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import org.jetbrains.annotations.NotNull;
import org.yupay.alexios.api.BookProcessor;

import java.io.IOException;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.yupay.alexios.google.GoogleUtils.*;

/**
 * Book processor for PDT710 data importation.To see the full spec visit
 * <a href="https://renta.sunat.gob.pe/empresas/herramientas-para-la-declaracion-formulario-virtual-710">
 * SUNAT PDT 710 microsite</a>.
 *
 * @author InfoYupay SACS
 * @version 1.0
 */
public final class PDTProcessor implements BookProcessor {

    /**
     * Creates an empty PDT processor object.
     */
    public PDTProcessor() {
    }

    @Override
    public void processSheet(Spreadsheet spreadsheet, Path target) throws Exception {
        var data = firstGridByName("030000", spreadsheet);
        var params = new PDTParams(
                stringAt(data, 4, 1),
                stringAt(data, 5, 1),
                decimalAt(data, 19, 1));

        do030300(firstSheetByName("030300", spreadsheet), params, target);
        do030500(firstSheetByName("030500", spreadsheet), params, target);
        do030600(firstSheetByName("030600", spreadsheet), params, target);
        do031200(firstSheetByName("031200", spreadsheet), params, target);
        do031300(firstSheetByName("031300", spreadsheet), params, target);
        doTrial031700(firstSheetByName("031700", spreadsheet), params, target);
    }

    /**
     * Computes a limit (threshold) to group some items using the UIT rate as a reference.
     *
     * @param uit the UIT rate value.
     * @return the value of 2 UIT (the limit established when this implementation was written).
     */
    private @NotNull BigDecimal getLimit(@NotNull BigDecimal uit) {
        return uit.multiply(new BigDecimal(2)).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Executes the receivable accounts (12 and 13) from LE0303 book.
     *
     * @param aSheet the sheet objects.
     * @param params parameters to perform duty.
     * @param target the target output folder path.
     * @throws IOException if unable to write.
     */
    private void do030300(Sheet aSheet, PDTParams params, Path target) throws IOException {
        if (!readInfoFlag(aSheet)) return;

        writeToFile(aSheet,
                params,
                361,
                7,
                "12",
                PDTFieldProcessor.receivable(),
                target
        );
        writeToFile(aSheet,
                params,
                362,
                7,
                "13",
                PDTFieldProcessor.receivable(),
                target
        );
    }

    /**
     * Executes the receivable accounts (16 and 17) from LE0305 book.
     *
     * @param aSheet the sheet objects.
     * @param params parameters to perform duty.
     * @param target the target output folder path.
     * @throws IOException if unable to write.
     */
    private void do030500(Sheet aSheet, PDTParams params, Path target) throws IOException {
        if (!readInfoFlag(aSheet)) return;

        writeToFile(aSheet,
                params,
                364,
                7,
                "16",
                PDTFieldProcessor.receivable(),
                target
        );

        writeToFile(aSheet,
                params,
                365,
                7,
                "17",
                PDTFieldProcessor.receivable(),
                target
        );
    }

    /**
     * Executes the doubtful account from LE0306 book.
     *
     * @param aSheet the sheet objects.
     * @param params parameters to perform duty.
     * @param target the target output folder path.
     * @throws IOException if unable to write.
     */
    private void do030600(Sheet aSheet, PDTParams params, Path target) throws IOException {
        if (!readInfoFlag(aSheet)) return;
        writeToFile(aSheet,
                params,
                true,
                367,
                -1,
                null,
                PDTFieldProcessor.doubtful(),
                target
        );
    }

    /**
     * Executes the payable accounts (42 and 43) from LE0312 book.
     *
     * @param aSheet the sheet objects.
     * @param params parameters to perform duty.
     * @param target the target output folder path.
     * @throws IOException if unable to write.
     */
    private void do031200(Sheet aSheet, PDTParams params, Path target) throws IOException {
        if (!readInfoFlag(aSheet)) return;
        writeToFile(aSheet,
                params,
                404,
                7,
                "42",
                PDTFieldProcessor.payableTrade(),
                target
        );
        writeToFile(aSheet,
                params,
                405,
                7,
                "43",
                PDTFieldProcessor.payableTrade(),
                target
        );
    }

    /**
     * Executes the payable accounts (46 and 47) from LE0313 book.
     *
     * @param aSheet the sheet objects.
     * @param params parameters to perform duty.
     * @param target the target output folder path.
     * @throws IOException if unable to write.
     */
    private void do031300(Sheet aSheet, PDTParams params, Path target) throws IOException {
        writeToFile(aSheet,
                params,
                407,
                8,
                "46",
                PDTFieldProcessor.payableOthers(),
                target);
        writeToFile(aSheet,
                params,
                408,
                8,
                "47",
                PDTFieldProcessor.payableOthers(),
                target);
    }

    /**
     * Executes the trial balance from LE0317 book.
     *
     * @param aSheet the sheet objects.
     * @param params parameters to perform duty.
     * @param target the target output folder path.
     * @throws IOException if unable to write.
     */
    private void doTrial031700(Sheet aSheet, PDTParams params, Path target) throws IOException {
        if (!readInfoFlag(aSheet)) return;
        var output = target.resolve(new TrialNameCompiler(params).get());
        recreateFile(output);
        try (var fos = Files.newOutputStream(output);
             var ps = new PrintStream(fos, true, StandardCharsets.UTF_8)) {
            aSheet.getData().getFirst()
                    .getRowData()
                    .stream()
                    .skip(3)
                    .map(RowData::getValues)
                    .filter(ignoreBlank())
                    .filter(c -> !c.getFirst().getFormattedValue().strip().equals("89"))
                    .map(new TrialConverter())
                    .forEach(ps::print);
        }
    }

    /**
     * Writes data from a given sheet into a txt file.
     *
     * @param aSheet         given sheet.
     * @param params         parameters for export.
     * @param fieldNum       the field (casilla) number.
     * @param flagIndex      column index where flag is located.
     * @param flag           the flag to check.
     * @param fieldProcessor the field processor to use (look for static factories).
     * @param target         the target output directory.
     * @throws IOException if writing operations fail.
     * @implNote a parameter header skip was inlined because always was 5. But,
     * in a future release, if changes in google sheetName templates are made,
     * then the header skip parameter may be required. That paremeter was to state
     * how many rows were header decorations and, thus, should be skipped from reading.
     */
    private void writeToFile(Sheet aSheet,
                             PDTParams params,
                             int fieldNum,
                             int flagIndex,
                             String flag,
                             PDTFieldProcessor fieldProcessor,
                             Path target
    ) throws IOException {
        writeToFile(aSheet, params, false, fieldNum, flagIndex, flag, fieldProcessor, target);
    }


    /**
     * Writes data from a given sheet into a txt file.
     *
     * @param aSheet         given sheet.
     * @param params         parameters for export.
     * @param checkAbs       flag to indicate wether should check for amount absolute value,
     *                       or check for amount read value when comparing with UIT limits.
     * @param fieldNum       the field (casilla) number.
     * @param flagIndex      column index where flag is located.
     * @param flag           the flag to check.
     * @param fieldProcessor the field processor to use (look for static factories).
     * @param target         the target output directory.
     * @throws IOException if writing operations fail.
     * @implNote a parameter header skip was inlined because always was 5. But,
     * in a future release, if changes in google sheetName templates are made,
     * then the header skip parameter may be required. That paremeter was to state
     * how many rows were header decorations and, thus, should be skipped from reading.
     */
    private void writeToFile(@NotNull Sheet aSheet,
                             @NotNull PDTParams params,
                             boolean checkAbs,
                             int fieldNum,
                             int flagIndex,
                             String flag,
                             PDTFieldProcessor fieldProcessor,
                             Path target
    ) throws IOException {

        //1. Collect data from sheet in buffer using .
        var buffer = aSheet.getData().getFirst()
                .getRowData().stream()
                .skip(5)
                .map(RowData::getValues)
                .filter(new FlagFilter(flagIndex, flag))
                .map(fieldProcessor)
                .collect(new PDTFieldCollector(getLimit(params.uit()), checkAbs));
        //1.a if no data was collected, then finish process without creating file.
        if (buffer.isEmpty()) return;

        //2. Check and delete output file.
        var output = target.resolve(
                new PDT710NameCompiler(params.year(), params.ruc(), fieldNum).get());
        recreateFile(output);

        //3. Open to write.
        try (var fos = Files.newOutputStream(output);
             var ps = new PrintStream(fos, true, StandardCharsets.UTF_8)) {
            //4. For each item, print.
            buffer.forEach(ps::print);
        }//5. Close file (autoclose).
        //Algorythm end.
    }

    /**
     * Inner record to hold the required parameters to perform duties.
     *
     * @param ruc  the tax payer ID.
     * @param year taxable year of reports.
     * @param uit  the uit rate value.
     * @author InfoYupay SACS
     * @version 1.0
     */
    record PDTParams(String ruc, String year, BigDecimal uit) {
    }
}
