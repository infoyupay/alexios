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
import com.google.api.services.sheets.v4.model.RowData;
import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import org.dvidal.alexios.api.BookProcessor;
import org.dvidal.alexios.google.GoogleUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import static org.dvidal.alexios.google.GoogleUtils.ignoreBlank;
import static org.dvidal.alexios.google.GoogleUtils.recreateFile;

public class BalanceProcessor implements BookProcessor {
    @Override
    public String title() {
        return "LE030000 - Libro de Inventarios y Balances";
    }

    @Override
    public void processSheet(Spreadsheet spreadsheet, File target) throws Exception {
        var params = new Params03(GoogleUtils.firstGridByName("030000", spreadsheet));
        for (var s : spreadsheet.getSheets()) {
            switch (s.getProperties().getTitle()) {
                case "030100" -> writeFinancial(s, params, "030100", target);
                case "030200" -> writeFile(s,
                        params,
                        new LE0302Converter(params),
                        ignoreBlank(),
                        4,
                        "030200",
                        target);
                case "030300" -> writeReceivable(s, params, "030300", target);
                case "030400" -> writeReceivable(s, params, "030400", target);
                case "030500" -> writeReceivable(s, params, "030500", target);
                case "030600" -> writeFile(s,
                        params,
                        new LE0306Converter(params),
                        ignoreBlank(),
                        5,
                        "030600",
                        target);
                case "030700" -> writeFile(s,
                        params,
                        new LE0307Converter(params),
                        ignoreBlank(),
                        5,
                        "030700",
                        target);
                case "030800" -> writeFile(s,
                        params,
                        new LE0308Converter(params),
                        ignoreBlank(),
                        5,
                        "030800",
                        target);
                case "030900" -> writeFile(s,
                        params,
                        new LE0309Converter(params),
                        ignoreBlank(),
                        2,
                        "030900",
                        target);
                case "031100" -> writeFile(s,
                        params,
                        new LE0311Converter(params),
                        ignoreBlank(),
                        5,
                        "031100",
                        target);
                case "031200" -> writeFile(s,
                        params,
                        new LE0312Converter(params),
                        ignoreBlank(),
                        5,
                        "031200",
                        target);
                case "031300" -> writeFile(s,
                        params,
                        new LE0313Converter(params),
                        ignoreBlank(),
                        5,
                        "031300",
                        target);
                case "031400" -> recreateFile(new File(target, params.compileFile("031400", false)));
                case "031500" -> writeFile(s,
                        params,
                        new LE0315Converter(params),
                        ignoreBlank(),
                        4,
                        "031500",
                        target);
                case "031601" -> new LE031601Processor(params, target, s).call();
                case "031602" -> writeFile(s,
                        params,
                        new LE031602Converter(params),
                        ignoreBlank(),
                        5,
                        "031602",
                        target);
                case "031700" -> writeFile(s,
                        params,
                        new LE031700Converter(params),
                        ignoreBlank(),
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

    private void writeFinancial(Sheet aSheet,
                                Params03 params,
                                String bookID,
                                File target) throws IOException {
        //With default converter.
        writeFinancial(aSheet, params, new FinancialConverter(params), bookID, target);
    }

    private void writeFinancial(Sheet aSheet,
                                Params03 params,
                                Function<List<CellData>, String> converter,
                                String bookID,
                                File target) throws IOException {
        writeFile(aSheet,
                params,
                //Provided converter for financial reports.
                converter,
                //Filter rows without entry ID.
                rw -> rw.size() > 2
                        && Optional.ofNullable(rw.get(2))
                        .map(CellData::getFormattedValue)
                        .filter(x -> !x.isBlank())
                        .isPresent(),
                //All financial reports has 2 header rows at this verison. (May change in future).
                2,
                bookID,
                target);
    }

    public void writeReceivable(Sheet aSheet, Params03 params, String bookID, File target) throws IOException {
        writeFile(aSheet, params, new ReceivableConverter(params), ignoreBlank(), 5, bookID, target);
    }

    private void writeFile(Sheet aSheet,
                           Params03 params,
                           Function<List<CellData>, String> converter,
                           Predicate<List<CellData>> filter,
                           long header,
                           String bookID,
                           File target) throws IOException {
        //Check info flag
        var info = readInfoFlag(aSheet);
        //Compile file name
        var output = new File(target, params.compileFile(bookID, info));
        //Recreate the file (check if exists to delete, then touch).
        recreateFile(output);
        //Open file to write in UTF-8.
        try (var fos = new FileOutputStream(output);
             var ps = new PrintStream(fos, true, StandardCharsets.UTF_8)) {
            //Convert and print.
            aSheet.getData().get(0).getRowData()
                    .stream()
                    .skip(header)
                    .map(RowData::getValues)
                    .filter(filter)
                    .map(converter)
                    .forEach(ps::print);
        }//Close file (autoclose).
        //END!
    }
}
