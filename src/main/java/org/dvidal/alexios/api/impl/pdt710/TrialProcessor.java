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

package org.dvidal.alexios.api.impl.pdt710;

import com.google.api.services.sheets.v4.model.CellData;

import java.util.List;
import java.util.StringJoiner;
import java.util.function.Function;

import static java.math.RoundingMode.HALF_EVEN;
import static org.dvidal.alexios.google.GoogleUtils.decimalFrom;

/**
 * Processor to read data from a google spreadsheet row into
 * a String as specified by SUNAT PDT710, Trial Balance.
 *
 * @implNote used reverse engenieering to know the data format since
 * a specification was not provided, but a helper excel file with macros
 * gave hints on the structure and file naming.
 */
final class TrialProcessor implements Function<List<CellData>, String> {
    @Override
    public String apply(List<CellData> cellData) {
        return new StringJoiner("|")
                .add(cellData.get(0).getFormattedValue())
                .add("%d".formatted(decimalFrom(cellData.get(2)).setScale(0, HALF_EVEN).intValueExact()))
                .add("%d".formatted(decimalFrom(cellData.get(3)).setScale(0, HALF_EVEN).intValueExact()))
                .add("%d".formatted(decimalFrom(cellData.get(4)).setScale(0, HALF_EVEN).intValueExact()))
                .add("%d".formatted(decimalFrom(cellData.get(5)).setScale(0, HALF_EVEN).intValueExact()))
                .add("%d".formatted(decimalFrom(cellData.get(10)).setScale(0, HALF_EVEN).intValueExact()))
                .add("%d".formatted(decimalFrom(cellData.get(11)).setScale(0, HALF_EVEN).intValueExact()))
                .add("0")
                .add("0")
                .add("\r\n")
                .toString();
    }
}
