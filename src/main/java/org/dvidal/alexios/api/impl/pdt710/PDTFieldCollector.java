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

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

/**
 * Collapses all PDTField elements into sumarized ones, all resulting elements
 * whose amounts are less than a uitLimit threshold, will be sumarized inside
 * a "99" identity object. If only one object is within that threshold, the
 * original data will prevail.
 *
 * @param uitLimit the uit limit threshold.
 * @param checkAbs the check abs flag. If set to true, sumarizing amounts below uitLimit
 *                 will be done comparing field.amount.abs(); if false, abs() wont be invoked.
 * @version 1.0
 */
record PDTFieldCollector(BigDecimal uitLimit,
                         boolean checkAbs)
        implements Collector<PDTField, ArrayList<PDTField>, List<PDTField>> {

    /**
     * Supplies an empty element with doiType 99 for accumulation.
     *
     * @return the PDTField object.
     */
    static @NotNull PDTField identity() {
        var r = new PDTField();
        r.doiType = "99";
        return r;
    }

    @Contract(pure = true)
    @Override
    public @NotNull Supplier<ArrayList<PDTField>> supplier() {
        return ArrayList::new;
    }

    @Contract(pure = true)
    @Override
    public @NotNull BiConsumer<ArrayList<PDTField>, PDTField> accumulator() {
        return this::accumulate;
    }

    /**
     * Convenient and delegated to accumulate. If
     * item exists in list, will sum amounts; else
     * will add to list.
     *
     * @param list the list where result should be put.
     * @param item the item to add into list.
     */
    private void accumulate(@NotNull ArrayList<PDTField> list, PDTField item) {
        var ix = list.indexOf(item);
        if (ix < 0) list.add(item);
        else list.get(ix).sumAmount(item.toAmount());
    }

    @Contract(pure = true)
    @Override
    public @NotNull BinaryOperator<ArrayList<PDTField>> combiner() {
        return (a, b) -> {
            //Use the accumulation function on each element of b.
            b.forEach(i -> accumulate(a, i));
            return a;
        };
    }

    @Contract(pure = true)
    @Override
    public @NotNull Function<ArrayList<PDTField>, List<PDTField>> finisher() {
        return ls -> {
            //This counter will increment with each item less than UIT threshold.
            var count = new Counter();
            //Will reduce all elements with less than UIT threshold amounts.
            var fold = ls
                    .stream()
                    .filter(this::lessThanUIT)
                    .peek(count::increment)
                    .reduce(identity(), PDTField::reduceAmount);
            //If more than one element was folded, should remove
            //folded elements and add the result of reduce.
            if (count.value > 1) {
                ls.removeIf(this::lessThanUIT);
                ls.add(fold);
            }
            //Swap to absolute value for every collected amount
            ls.forEach(p -> p.amount = p.amount.abs());
            //Return the final version of the recollected elements.
            return ls;
        };
    }

    /**
     * Checks if an item amount is less than the UIT limit threshold.
     *
     * @param item the item to check.
     * @return true if item amount is less than UIT limit threshold.
     */
    private boolean lessThanUIT(PDTField item) {
        if (checkAbs) return item.amount.abs().compareTo(uitLimit) < 0;
        else return item.amount.compareTo(uitLimit) < 0;
    }

    @Contract(value = " -> new", pure = true)
    @Override
    public @NotNull @Unmodifiable Set<Characteristics> characteristics() {
        return Set.of(Characteristics.UNORDERED);
    }

    /**
     * Convenient private class to allow counting.
     *
     * @version 1.0
     */
    private static class Counter {
        /**
         * The counted value.
         */
        int value = 0;

        /**
         * Convenient method to increment
         * the value from a lambda consumer.
         *
         * @param element ignored item from consumer.
         * @param <T>     type erasure of consumer.
         */
        <T> void increment(T element) {
            value++;
        }
    }
}
