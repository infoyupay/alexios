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

package org.yupay.alexios.api.impl.costs;

import com.google.api.services.sheets.v4.model.Spreadsheet;
import org.jetbrains.annotations.NotNull;
import org.yupay.alexios.api.BookProcessor;

import java.nio.file.Path;

import static org.yupay.alexios.google.GoogleUtils.exportFile;
import static org.yupay.alexios.google.GoogleUtils.infoFlag;

/**
 * The BookProcessor implementation to process Costs book
 * by PLE 100000 specification.
 *
 * @version 1.0
 */
public class CostsProcessor implements BookProcessor {

    /**
     * Creates an empty costs book processor.
     */
    public CostsProcessor() {
    }

    @Override
    public void processSheet(@NotNull Spreadsheet spreadsheet, Path target) throws Exception {
        //Extract parameters from LE100000 sheet.
        var params = spreadsheet.getSheets().stream()
                .filter(ss -> "LE100000".equals(ss.getProperties().getTitle()))
                .findAny()
                .map(LE1000Params::fromSheet)
                .orElseThrow(() -> new IllegalArgumentException("Cannot find LE100000 worksheet."));

        //Check all worksheets.
        for (var worksheet : spreadsheet.getSheets()) {

            switch (worksheet.getProperties().getTitle()) {
                case "100100" -> exportFile(worksheet,
                        3L,
                        compileName(params, "100100", infoFlag(worksheet)),
                        target,
                        new LE1001Converter(params.year()));
                case "100200" -> exportFile(worksheet,
                        3L,
                        compileName(params, "100200", infoFlag(worksheet)),
                        target,
                        new LE1002Converter(params.year()));
                case "100300" -> exportFile(worksheet,
                        3L,
                        compileName(params, "100300", infoFlag(worksheet)),
                        target,
                        new LE1003Converter(params.year()));
                case "100400" -> exportFile(worksheet,
                        3L,
                        compileName(params, "100400", infoFlag(worksheet)),
                        target,
                        new LE1004Converter(params.year()));
            }
        }
    }

    /**
     * Process the parameters to create an appropiate txt file name as specified by PLE.
     *
     * @param params the parameters of the main worksheet.
     * @param bookID the book ID.
     * @param info   the info flag (if false=no information is sent; true otherwise).
     * @return the file name.
     */
    private @NotNull String compileName(@NotNull LE1000Params params, String bookID, boolean info) {
        return "LE%s%s0000%s00%s%s11.TXT".formatted(
                params.ruc(),
                params.year(),
                bookID,
                params.ops(),
                info ? "1" : "0");
    }
}
