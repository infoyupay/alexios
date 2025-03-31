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

import com.google.api.services.sheets.v4.model.CellData;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Convenient filter to retain only those elements
 * with given flag.
 *
 * @param flag  The flag to find. If less than zero, will ever return true.
 * @param index the column index (0-based) where flag is.
 * @version 1.0
 */
record FlagFilter(int index, String flag) implements Predicate<List<CellData>> {
    @Override
    public boolean test(@NotNull List<CellData> cellData) {
        //Check if first column is null, empty or blank.
        //If so, ignore row by returning false.
        if (cellData.isEmpty()) return false;
        var first = cellData.getFirst().getFormattedValue();
        if (first==null || first.isBlank()) return false;

        //Check data
        return index < 0 //If index is less than 0, no flag column is present, always true.
                ||
                (cellData.size() > index
                        && Optional.ofNullable(cellData.get(index))
                        .map(CellData::getFormattedValue)
                        .map(String::strip)
                        .filter(flag::equals)
                        .isPresent());
    }
}
