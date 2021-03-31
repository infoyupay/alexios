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

import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import org.dvidal.alexios.api.BookProcessor;
import org.dvidal.alexios.google.GoogleUtils;

import java.io.File;
import java.io.IOException;

public class LE030000Processor implements BookProcessor {
    @Override
    public String title() {
        return "LE030000 - Libro de Inventarios y Balances";
    }

    @Override
    public void processSheet(Spreadsheet spreadsheet, File target) throws Exception {
        var params = new Params03(GoogleUtils.firstGridByName("030000",spreadsheet));
        spreadsheet.getSheets()
                .forEach(s->{
                    switch (s.getProperties().getTitle()){
                        case "030100"->;
                        case "030200"->;
                        case "030300"->;
                        case "030400"->;
                        case "030500"->;
                        case "030600"->;
                        case "030700"->;
                        case "030800"->;
                        case "030900"->;
                        case "031100"->;
                        case "031200"->;
                        case "031300"->;
                        case "031400"->;
                        case "031500"->;
                        case "031601"->;
                        case "031602"->;
                        case "031700"->;
                        case "031800"->;
                        case "031900"->;
                        case "032000"->;
                        case "032300"->;
                        case "032400"->;
                    }
                });
    }
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void writeFinancial(Sheet aSheet, Params03 params, String bookID, File target) throws IOException {
        var info = readInfoFlag(aSheet);
        var output = new File(target, params.compileFile(bookID, info));
        if (output.exists())output.delete();
        output.createNewFile();

    }
}
