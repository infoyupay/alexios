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
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.StringJoiner;
import java.util.function.Function;

import static org.dvidal.alexios.google.GoogleUtils.decimalText;

/**
 * Function to convert a row of CellData into a SUNAT-PLE txt tuple for
 * LE031700 - trial balance.
 * <br/>
 * It shall add \r\n at the end of line.
 *
 * @param params parameters to perform conversion.
 * @author InfoYupay SACS
 * @version 1.0
 */
record LE031700Converter(Params03 params) implements Function<List<CellData>, String> {

    @Override
    public String apply(@NotNull List<CellData> cellData) {
        return new StringJoiner("|")
                .add(params().periodID())//1
                .add(cellData.get(0).getFormattedValue())//2
                .add(decimalText(cellData.get(2)))//3
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
                .add(decimalText(cellData.get(15)))//16
                .add(decimalText(cellData.get(16)))//17
                .add(decimalText(cellData.get(17)))//18
                .add("1")//19
                .add("\r\n")
                .toString();
    }
}
