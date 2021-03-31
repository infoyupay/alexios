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

import static org.dvidal.alexios.google.GoogleUtils.decimalText;

record LE031900Converter(Params03 params) implements Function<List<CellData>, String> {
    @Override
    public String apply(List<CellData> cellData) {
        return new StringJoiner("|")
                .add(params.periodID())//1
                .add("09")//2
                .add(cellData.get(2).getFormattedValue())//3
                .add(decimalText(cellData.get(3)))//4
                .add(decimalText(cellData.get(4)))//5
                .add(decimalText(cellData.get(5)))//6
                .add(decimalText(cellData.get(6)))//7
                .add(decimalText(cellData.get(7)))//8
                .add(decimalText(cellData.get(8)))//9
                .add(decimalText(cellData.get(9)))//10
                .add(decimalText(cellData.get(10)))//11
                .add(decimalText(cellData.get(11)))//12
                .add(decimalText(cellData.get(12)))//13
                .add(decimalText(cellData.get(13)))//14
                .add(decimalText(cellData.get(14)))//15
                .add("1")//16
                .add("\r\n")
                .toString();
    }
}
