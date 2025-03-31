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

package org.yupay.alexios.api.impl.pdt710;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * A supplier which solely purpose is to provide with the appropiate
 * valid name for PDT710 importation specification.
 *
 * @param field the field code as an int.
 * @param ruc   the ruc number (tax ID).
 * @param year  the year of tax period as a String.
 * @version 1.0
 */
record PDT710NameCompiler(String year, String ruc, int field) implements Supplier<String> {
    @Contract(pure = true)
    @Override
    public @NotNull String get() {
        return "0710%s%s%03d.txt".formatted(year, ruc, field);
    }
}
