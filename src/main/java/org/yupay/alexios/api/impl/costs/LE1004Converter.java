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
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

/**
 * Inner implementation of function to convert a row in the form
 * of a {@code List<CellData>} into a String for a PLE tuple
 * as specified by book 100400.
 * It shall add \r\n at the end of line.
 *
 * @version 1.0
 */
final class LE1004Converter implements Function<List<CellData>, String> {
    /**
     * The year of the tax period.
     */
    private final String year;
    /**
     * An atomic counter to generate correlatives.
     */
    private final AtomicLong correlative = new AtomicLong();

    /**
     * Default constructor.
     *
     * @param year the tax period year. "0000" will be appended
     *             to match the PLE specification.
     */
    public LE1004Converter(String year) {
        this.year = year + "0000";
    }

    @Override
    public @NotNull String apply(@NotNull List<CellData> cellData) {
        var line = new String[8];
        line[0] = year;
        line[1] = "%024d".formatted(correlative.incrementAndGet());
        line[2] = cellData.get(0).getFormattedValue();
        line[3] = cellData.get(1).getFormattedValue();
        line[4] = cellData.get(2).getFormattedValue();
        line[5] = cellData.get(3).getFormattedValue();
        line[6] = "1";
        line[7] = "\r\n";
        return String.join("|", line);
    }
}
