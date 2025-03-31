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

package org.yupay.alexios.api.impl.costs;

import org.yupay.alexios.google.GoogleUtils;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

class CostsProcessorTest {
    static final String costID = "1Q1ykd6IKSTtqVDOaV19lYN6vWU1nUPS_2aQirg0-Kd8";

    @Test
    void testExport() throws Exception {
        var sheet = GoogleUtils.getSpreadsheet(costID);
        var output = Path.of("/home/dvidal/Público/DIAR/2024/tm", "LE100000");
        //if (!output.exists()) System.out.printf("output mkdirs: %b%n", output.mkdirs());
        new CostsProcessor().processSheet(sheet, output);
    }
}
