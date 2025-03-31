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

package org.yupay.alexios.tools;

import org.yupay.alexios.google.GoogleUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class DoiNumberCheckTest {
    static final String balanceID = "1aAL_2ybiGuOI5DE8GczAYdbppSmgqPebJirgYjxdBok";

    @Test
    void testCheckLE03() throws GeneralSecurityException, IOException {
        var sheet = GoogleUtils.getSpreadsheet(balanceID);
        var ls = new DoiNumberCheck().processLE03(sheet);
        ls.forEach((s, l) -> {
            System.out.println("Checking Sheet " + s);
            l.stream()
                    .map(r
                            -> "%d row failed: %s - %s".formatted(
                            r.rowIndex(),
                            r.doiType(),
                            r.doiNumber()))
                    .forEach(System.out::println);
            System.out.println("=".repeat(50));
        });
    }
}
