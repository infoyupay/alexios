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
import java.util.Objects;
import java.util.StringJoiner;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

import static org.dvidal.alexios.google.GoogleUtils.*;

/**
 * Function to convert a row of CellData into a SUNAT-PLE txt tuple for
 * LE030800 - movable investments.
 * <br/>
 * It shall add \r\n at the end of line.
 *
 * @author InfoYupay SACS
 * @version 1.0
 */
final class LE0308Converter implements Function<List<CellData>, String> {
    private final Params03 params;
    private final AtomicLong correlative = new AtomicLong(0);

    /**
     * Canonical constructor.
     *
     * @param params the parameters to perform formatting.
     */
    LE0308Converter(Params03 params) {
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
                .add(cellData.get(4).getFormattedValue())//7
                .add(decimalText(cellData.get(6)))//8
                .add("%d".formatted(intFromCell(cellData.get(7))))//9
                .add(decimalText(cellData.get(8)))//10
                .add(decimalText(cellData.get(9)))//11
                .add("1")//12
                .add(safeText(cellData, 10))
                .add("\r\n")
                .toString();
    }

    /**
     * Accessor for params.
     *
     * @return parameters to perform conversion.
     */
    public Params03 params() {
        return params;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (LE0308Converter) obj;
        return Objects.equals(this.params, that.params);
    }

    @Override
    public int hashCode() {
        return Objects.hash(params);
    }

    @Override
    public String toString() {
        return "LE0308Converter[" +
                "params=" + params + ']';
    }

}
