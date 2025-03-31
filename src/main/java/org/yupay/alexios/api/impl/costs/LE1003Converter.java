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

import static org.yupay.alexios.google.GoogleUtils.readDoubleBehind;

/**
 * An inner function implementation to convert the google sheet row
 * in the form of a {@code List<CellData>} into a String as specified
 * by the PLE 100300 book.
 * It shall add \r\n at the end of line.
 *
 * @param year the tax year of the report.
 * @version 1.0
 */
record LE1003Converter(String year) implements Function<List<CellData>, String> {
    /**
     * Default constructor. "0000" will be appended to
     * the year to match PLE specification.
     *
     * @param year the year of the tax Period.
     */
    LE1003Converter(String year) {
        this.year = year + "0000";
    }

    @Override
    public @NotNull String apply(@NotNull List<CellData> cellData) {
        var line = new String[14];
        line[0] = year;
        line[1] = cellData.get(0).getFormattedValue();
        line[2] = "%.100s".formatted(cellData.get(1).getFormattedValue());
        readDoubleBehind(line, 3, 10, cellData);
        line[11] = cellData.get(10).getFormattedValue();
        line[12] = "1";
        line[13] = "\r\n";
        return String.join("|", line);
    }
}
