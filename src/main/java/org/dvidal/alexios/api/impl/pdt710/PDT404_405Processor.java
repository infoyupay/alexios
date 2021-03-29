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

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Function;

record PDT404_405Processor() implements Function<List<CellData>, PDTField> {
    private static final NamesProcessor onName = new NamesProcessor(4);
    @Override
    public PDTField apply(List<CellData> cellData) {
        var r = new PDTField();
        r.parseDoiType(cellData.get(0).getFormattedValue());
        r.doiNum = cellData.get(2).getFormattedValue();
        onName.accept(cellData, r);
        r.amount = BigDecimal.valueOf(cellData.get(5).getEffectiveValue().getNumberValue());
        return r;
    }
}
