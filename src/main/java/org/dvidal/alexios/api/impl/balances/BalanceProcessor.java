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

package org.dvidal.alexios.api.impl.balances;

import com.google.api.services.sheets.v4.model.CellData;
import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import org.dvidal.alexios.api.BookProcessor;
import org.dvidal.alexios.google.GoogleUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static org.dvidal.alexios.google.GoogleUtils.exportFile;
import static org.dvidal.alexios.google.GoogleUtils.recreateFile;

/**
 * Implementation for LE030000 - Balances book.
 * See PLE specification 030000.
 *
 * @author InfoYupay SACS
 * @version 1.0
 */
public class BalanceProcessor implements BookProcessor {

    @Override
    public void processSheet(Spreadsheet spreadsheet, Path target) throws Exception {
        //Extract parameters from first worksheet.
        var params = new Params03(GoogleUtils.firstGridByName("030000", spreadsheet));
        //Iterate through sheets.
        for (var s : spreadsheet.getSheets()) {
            switch (s.getProperties().getTitle()) {
                case "030100" -> writeFinancial(s, params, "030100", target);
                case "030200" -> writeGenericFile(s,
                        params,
                        new LE0302Converter(params),
                        4,
                        "030200",
                        target);
                case "030300" -> writeReceivable(s, params, "030300", target);
                case "030400" -> writeReceivable(s, params, "030400", target);
                case "030500" -> writeReceivable(s, params, "030500", target);
                case "030600" -> writeGenericFile(s,
                        params,
                        new LE0306Converter(params),
                        5,
                        "030600",
                        target);
                case "030700" -> writeGenericFile(s,
                        params,
                        new LE0307Converter(params),
                        5,
                        "030700",
                        target);
                case "030800" -> writeGenericFile(s,
                        params,
                        new LE0308Converter(params),
                        5,
                        "030800",
                        target);
                case "030900" -> writeGenericFile(s,
                        params,
                        new LE0309Converter(params),
                        2,
                        "030900",
                        target);
                case "031100" -> writeGenericFile(s,
                        params,
                        new LE0311Converter(params),
                        5,
                        "031100",
                        target);
                case "031200" -> writeGenericFile(s,
                        params,
                        new LE0312Converter(params),
                        5,
                        "031200",
                        target);
                case "031300" -> writeGenericFile(s,
                        params,
                        new LE0313Converter(params),
                        5,
                        "031300",
                        target);
                case "031400" -> recreateFile(target.resolve(params.compileFile("031400", false)));
                case "031500" -> writeGenericFile(s,
                        params,
                        new LE0315Converter(params),
                        4,
                        "031500",
                        target);
                case "031601" -> new LE031601Processor(params, target, s).call();
                case "031602" -> writeGenericFile(s,
                        params,
                        new LE031602Converter(params),
                        5,
                        "031602",
                        target);
                case "031700" -> writeGenericFile(s,
                        params,
                        new LE031700Converter(params),
                        3,
                        "031700",
                        target);
                case "031800" -> writeFinancial(s, params, "031800", target);
                case "031900" -> writeFinancial(s, params, new LE031900Converter(params), "031900", target);
                case "032000" -> writeFinancial(s, params, "032000", target);
                case "032300" -> new LE0323Processor(params, target, s).call();
                case "032400" -> writeFinancial(s, params, "032400", target);
                case "032500" -> writeFinancial(s, params, "032500", target);
            }
        }
    }

    /**
     * Writes the financial reports. The balances book contains the following financial reports:
     * <ul>
     *     <li><b>0301:</b> Financial statements (balance sheet).</li>
     *     <li><b>0318:</b> Cash flow - direct method.</li>
     *     <li><b>0319:</b> Equity changes.</li>
     *     <li><b>0320:</b> Income statement.</li>
     *     <li><b>0324:</b> Comprehensive income statement.</li>
     *     <li><b>0325:</b> Cash flow - indirect method.</li>
     * </ul>
     *
     * @param aSheet worksheet object.
     * @param params parameters to perform exportation.
     * @param bookID the ID of the book.
     * @param target the target path (folder).
     * @throws IOException if unable to write file.
     */
    private void writeFinancial(Sheet aSheet,
                                Params03 params,
                                String bookID,
                                Path target) throws IOException {
        //With default converter.
        writeFinancial(aSheet, params, new FinancialConverter(params), bookID, target);
    }

    /**
     * The {@link #writeFinancial(Sheet, Params03, String, Path)} relies upon this implementation
     * to perform its duties. It's necessary because the financial report LE031900 (Equity changes)
     * requires further customization which is achieved by {@link LE031900Converter}. So, in order
     * to be able to use said customization, this method becomes necessary. All other use cases
     * shall use the {@link FinancialConverter} default function.
     *
     * @param aSheet    worksheet object.
     * @param params    parameters to export.
     * @param converter the converter to format cell data into SUNAT-PLE format.
     * @param bookID    the electronic book ID.
     * @param target    output path (directory).
     * @throws IOException if unable to write in output path.
     */
    private void writeFinancial(Sheet aSheet,
                                @NotNull Params03 params,
                                Function<List<CellData>, String> converter,
                                String bookID,
                                Path target) throws IOException {
        var info = GoogleUtils.infoFlag(aSheet);
        exportFile(
                aSheet,
                2,
                params.compileFile(bookID, info),
                target,
                converter,
                //Filter rows without entry ID.
                rw -> rw.size() > 2
                        && Optional.ofNullable(rw.get(2))
                        .map(CellData::getFormattedValue)
                        .filter(x -> !x.isBlank())
                        .isPresent());
    }

    /**
     * Inner method to write receivable statements. Receivable statements conatins the same structure, so
     * one single converter may be reused.
     *
     * @param aSheet worsheet object.
     * @param params parameters for the exportation process.
     * @param bookID PLE book ID.
     * @param target the target output directory.
     * @throws IOException if unable to write to target.
     */
    private void writeReceivable(Sheet aSheet,
                                 @NotNull Params03 params,
                                 String bookID,
                                 Path target) throws IOException {
        var info = GoogleUtils.infoFlag(aSheet);
        exportFile(
                aSheet,
                5,
                params.compileFile(bookID, info),
                target,
                new ReceivableConverter(params));
    }

    /**
     * Inner method where the writing is handled to allow better customization.
     *
     * @param aSheet    the worksheet object.
     * @param params    parameters for exportation.
     * @param converter the converter to format values.
     * @param header    the header lines to skip.
     * @param bookID    the ID of the book.
     * @param target    the target output folder.
     * @throws IOException if unable to create/write new file.
     */
    private void writeGenericFile(Sheet aSheet,
                                  @NotNull Params03 params,
                                  Function<List<CellData>, String> converter,
                                  long header,
                                  String bookID,
                                  @NotNull Path target) throws IOException {
        //Check info flag
        var info = readInfoFlag(aSheet);
        exportFile(
                aSheet,
                header,
                params.compileFile(bookID, info),
                target,
                converter);
    }
}
