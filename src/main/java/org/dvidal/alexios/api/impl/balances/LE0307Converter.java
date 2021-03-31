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
import static org.dvidal.alexios.google.GoogleUtils.decimalText1602;

record LE0307Converter(Params03 params) implements Function<List<CellData>, String> {
    @Override
    public String apply(List<CellData> cellData) {
        return new StringJoiner("|")
                .add(params.periodID())//1
                .add(cellData.get(0).getFormattedValue())//2
                .add(cellData.get(4).getFormattedValue())//3
                .add(cellData.get(2).getFormattedValue())//4
                .add("1")//5
                .add(cellData.get(3).getFormattedValue())//6
                .add("%.80s".formatted(cellData.get(6).getFormattedValue()))//7
                .add(cellData.get(7).getFormattedValue())//8
                .add(cellData.get(9).getFormattedValue())//9
                .add(decimalText1602(cellData.get(11)))//10
                .add(decimalText1602(cellData.get(12)))//11
                .add(decimalText(cellData.get(13)))//12
                .add("1")//13
                .add("\r\n")
                .toString();
    }
}
