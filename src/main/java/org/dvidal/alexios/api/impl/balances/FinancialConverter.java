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

import java.util.List;
import java.util.StringJoiner;
import java.util.function.Function;

import static org.dvidal.alexios.google.GoogleUtils.decimalFrom;

/**
 * Function to convert financial status reports (except equity changes report).
 *
 * @param params parameters data for LE030000 report (first worksheet).
 */
record FinancialConverter(Params03 params) implements Function<List<CellData>, String> {
    @Override
    public String apply(List<CellData> cellData) {
        return new StringJoiner("|")
                .add(params.periodID())
                .add("01")
                .add(cellData.get(2).getFormattedValue())
                .add("%.2f".formatted(decimalFrom(cellData.get(3))))
                .add("\r\n")
                .toString();
    }
}
