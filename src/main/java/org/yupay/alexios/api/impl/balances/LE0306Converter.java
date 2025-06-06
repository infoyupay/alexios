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
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

import static org.yupay.alexios.google.GoogleUtils.decimalText;
import static org.yupay.alexios.google.GoogleUtils.fromDateCell;

/**
 * This function converts a row of celldata into a String for the SUNAT-PLE txt specs
 * LE030600 - doubtful accounts (estimating).
 * <br/>
 * It shall add \r\n at end of line.
 *
 * @author InfoYupay SACS
 * @version 1.0
 */
final class LE0306Converter implements Function<List<CellData>, String> {
    final Params03 params;
    final AtomicLong correlative = new AtomicLong(0);

    /**
     * Canonical constructor.
     *
     * @param params pass the parameters for the formatting.
     */
    public LE0306Converter(Params03 params) {
        this.params = params;
    }

    @Override
    public String apply(@NotNull List<CellData> cellData) {
        return new StringJoiner("|")
                .add(params.periodID())//1
                .add(UUID.randomUUID().toString())//2
                .add("M%09d".formatted(correlative.incrementAndGet()))//3
                .add(cellData.get(0).getFormattedValue())//4
                .add(cellData.get(2).getFormattedValue())//5
                .add("%.100S".formatted(cellData.get(3).getFormattedValue()))//6
                .add(cellData.get(4).getFormattedValue())//7
                .add(cellData.get(6).getFormattedValue())//8
                .add(cellData.get(7).getFormattedValue())//9
                .add(fromDateCell(cellData.get(8)))//10
                .add(decimalText(cellData.get(9)))//11
                .add("1")//12
                .add("\r\n")
                .toString();
    }
}
