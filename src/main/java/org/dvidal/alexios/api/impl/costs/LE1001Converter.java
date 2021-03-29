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

package org.dvidal.alexios.api.impl.costs;

import com.google.api.services.sheets.v4.model.CellData;

import java.util.List;
import java.util.function.Function;

import static org.dvidal.alexios.google.GoogleUtils.readDoubleBehind;

/**
 * Converts the CellData of a row into a String line of PLE 100100 book.
 *
 * @version 1.0
 */
final record LE1001Converter(String year) implements Function<List<CellData>, String> {
    /**
     * Constructor of this class.
     *
     * @param year the year of the tax period.
     */
    LE1001Converter(String year) {
        this.year = year + "0000";
    }

    @Override
    public String apply(List<CellData> cellData) {
        var line = new String[7];
        line[0] = year;
        readDoubleBehind(line, 1, 4, cellData);
        line[5] = "1";
        line[6] = "\r\n";
        return String.join("|", line);
    }
}
