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

import com.google.api.services.sheets.v4.model.CellData;

import java.util.List;
import java.util.function.BiConsumer;

/**
 * Convenient biconsumer that takes row cell data at a given column index,
 * in that column index is stored a name; then, checks the cell data
 * doi type and num to determine if the name string should be treated
 * as a full name (lastnames, firstname middlename) or a legal entity
 * name (A business name). Finally, sets the PDTField name properties
 * in consequence.
 *
 * @version 1.0
 */
record NamesProcessor(int index) implements BiConsumer<List<CellData>, PDTField> {
    @Override
    public void accept(List<CellData> cellData, PDTField pdtField) {
        if ((pdtField.doiType.equals("06") && pdtField.doiNum.startsWith("20"))
                || pdtField.doiType.equals("00")) {
            pdtField.inputFlag = "0";
            pdtField.legalName = cellData.get(index).getFormattedValue();
        } else {
            pdtField.inputFlag = "1";
            var nameFull = cellData.get(index).getFormattedValue();
            if (nameFull.contains(",")) {
                var nameParts = nameFull.split(",");
                pdtField.name = nameParts[1];
                var lastParts = nameParts[0].split(" ", 2);
                pdtField.lastName1 = lastParts[0];
                if (lastParts.length ==2) pdtField.lastName2 = lastParts[1];
            } else {
                var spacedParts = nameFull.split(" ", 3);
                switch (spacedParts.length) {
                    case 3 -> {
                        pdtField.lastName1 = spacedParts[0];
                        pdtField.lastName2 = spacedParts[1];
                        pdtField.name = spacedParts[2];
                    }
                    case 2 -> {
                        pdtField.lastName1 = spacedParts[0];
                        pdtField.name = spacedParts[1];
                    }
                }
            }
        }
    }
}
