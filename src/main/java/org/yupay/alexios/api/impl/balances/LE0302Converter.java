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

package org.yupay.alexios.api.impl.balances;

import com.google.api.services.sheets.v4.model.CellData;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.StringJoiner;
import java.util.function.Function;

import static org.yupay.alexios.google.GoogleUtils.*;

/**
 * This function converts a row of cell data into SUNAT-PLE LE030200 txt specification.
 * It shall add \r\n at the end of line.
 *
 * @param params parameters to perform conversion.
 * @author InfoYupay SACS
 * @version 1.0
 */
record LE0302Converter(Params03 params) implements Function<List<CellData>, String> {
    @Override
    public String apply(@NotNull List<CellData> cellData) {
        return new StringJoiner("|")
                .add(params.periodID())//1
                .add(numericText(cellData.get(0)))//2
                .add(cellData.get(1).getFormattedValue())//3
                .add(cellData.get(3).getFormattedValue())//4
                .add(cellData.get(4).getFormattedValue())//5
                .add(decimalText(cellData.get(5)))//6
                .add(cellData.get(6).getFormattedValue())//7
                .add("1")//8
                .add(safeText(cellData, 7))//9
                .add("\r\n")
                .toString();
    }
}
