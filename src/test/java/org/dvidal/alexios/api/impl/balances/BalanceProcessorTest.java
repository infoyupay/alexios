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

import org.dvidal.alexios.google.GoogleUtils;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

class BalanceProcessorTest {
    static final String balanceID = "1H1eky7_STw--tIu_T-l8Q89ES7wPvz6CkmB4pzYxlyQ";

    @Test
    void testExport() throws Exception {
        var sheet = GoogleUtils.getSpreadsheet(balanceID);
        var output = Path.of("testout", "LE030000");
        //if (!output.exists()) System.out.printf("output mkdirs: %b%n", output.mkdirs());
        new BalanceProcessor().processSheet(sheet, output);
    }
}
