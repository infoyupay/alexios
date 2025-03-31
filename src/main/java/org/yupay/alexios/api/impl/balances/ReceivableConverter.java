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

import static org.yupay.alexios.google.GoogleUtils.*;

/**
 * Function to convert a row of CellData into a SUNAT-PLE tuple row for receivable statements.
 * When implementing this class, those receivable statements were:
 * <ul>
 *     <li><b>LE0303</b> Customers receivable.</li>
 *     <li><b>LE0304</b> Employees and shareholders receivable.</li>
 *     <li><b>LE0305</b> Others receivable.</li>
 * </ul>
 *
 * @author InfoYupay SACS
 * @version 1.0
 */
final class ReceivableConverter implements Function<List<CellData>, String> {
    private final Params03 params;
    private final AtomicLong correlative = new AtomicLong(0);

    /**
     * Canonical constructor.
     *
     * @param params parameters to perform conversion.
     */
    ReceivableConverter(Params03 params) {
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
                .add("%.100s".formatted(cellData.get(3).getFormattedValue()))//6
                .add(fromDateCell(cellData.get(4)))//7
                .add(decimalText(cellData.get(5)))//8
                .add("1")//9
                .add(safeText(cellData, 6))//10
                .add("\r\n")
                .toString();
    }
}
