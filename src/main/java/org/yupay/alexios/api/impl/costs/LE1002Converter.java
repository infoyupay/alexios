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

import com.google.api.services.sheets.v4.model.CellData;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Function;

import static org.yupay.alexios.google.GoogleUtils.*;

/**
 * Inner function to take a row as a {@code List<CellData>} and convert
 * it into a pretty plain text tuple as specified by PLE 100200 book.
 * It shall add \r\n at the end of line.
 *
 * @param year the tax year of the report.
 * @version 1.0
 */
record LE1002Converter(String year) implements Function<List<CellData>, String> {
    /**
     * Default constructor.
     *
     * @param year the tax period year.
     */
    LE1002Converter {
    }

    @Override
    public @NotNull String apply(@NotNull List<CellData> cellData) {
        var line = new String[9];
        line[0] = "%s%02d00".formatted(year, intFromCell(cellData.get(0)));
        line[1] = "%.2f".formatted(doubleFromCell(cellData.get(2)));
        readDoubleAhead(line, 2, 6, cellData);
        line[7] = "1";
        line[8] = "\r\n";
        return String.join("|", line);
    }
}
