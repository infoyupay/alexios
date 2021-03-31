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
import static org.dvidal.alexios.google.GoogleUtils.intFromCell;

record LE031602Converter(Params03 params) implements Function<List<CellData>, String> {
    @Override
    public String apply(List<CellData> cellData) {
        return new StringJoiner("|")
                .add(params.periodID())//1
                .add(cellData.get(0).getFormattedValue())//2
                .add(cellData.get(2).getFormattedValue())//3
                .add(cellData.get(3).getFormattedValue())//4
                .add("%.100s".formatted(cellData.get(5).getFormattedValue()))//5
                .add("%d".formatted(intFromCell(cellData.get(6))))//6
                .add("%.8f".formatted(decimalFrom(cellData.get(7))))//7
                .add("1")//8
                .add("\r\n")
                .toString();
    }
}
