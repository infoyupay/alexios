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

import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

/**
 * Collects all PDTField elements into one sumarized with 99
 * doiType and an amount that is the sum of the parcial amounts.
 * This is useful to sumarize amounts less than 3*UIT as specified
 * in the PDT 710 spec.
 *
 * @version 1.0
 */
class PDTFieldCollector implements Collector<PDTField, PDTField, PDTField> {
    /**
     * Supplies an empty element with doiType 99 for accumulation.
     *
     * @return the PDTField object.
     */
    static PDTField identity() {
        var r = new PDTField();
        r.doiType = "99";
        return r;
    }

    @Override
    public Supplier<PDTField> supplier() {
        return PDTFieldCollector::identity;
    }

    @Override
    public BiConsumer<PDTField, PDTField> accumulator() {
        return (a, b) -> a.sumAmount(b.amount);
    }

    @Override
    public BinaryOperator<PDTField> combiner() {
        return (a, b) -> a.sumAmount(b.amount);
    }

    @Override
    public Function<PDTField, PDTField> finisher() {
        return a -> {
            a.doiType = "99";
            a.doiNum = "";
            a.inputFlag = "";
            a.lastName1 = "";
            a.lastName2 = "";
            a.name = "";
            a.legalName = "";
            return a;
        };
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Set.of(Characteristics.UNORDERED);
    }
}
