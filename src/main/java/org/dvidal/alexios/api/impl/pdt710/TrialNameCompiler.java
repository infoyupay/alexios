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

import java.util.function.Supplier;

/**
 * A convenient implementation to create a propper filename for trial balance.
 *
 * @implNote the filename pattern was deducted via reverse engenieering from
 * macros in a helper file for Trial Balance provided by SUNAT.
 */
record TrialNameCompiler(PDTProcessor.PDTParams params) implements Supplier<String> {

    @Override
    public String get() {
        return "0710%s%s.txt".formatted(params.ruc(), params.year());
    }
}
