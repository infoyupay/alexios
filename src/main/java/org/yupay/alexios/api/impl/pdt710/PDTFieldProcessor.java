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

import com.google.api.services.sheets.v4.model.CellData;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Function;

/**
 * A function to process GoogleSheets rows from PLE specification into
 * POJO PDTField for collecting data.
 *
 * @version 1.0
 */
record PDTFieldProcessor(NamesProcessor onName, int amountIx) implements Function<List<CellData>, PDTField> {
    /**
     * Convenient constructor to initialize a custom names processor
     * based on a column index for names.
     *
     * @param nameIX   the column index for names.
     * @param amountIx the column index for ammounts.
     */
    PDTFieldProcessor(int nameIX, int amountIx) {
        this(new NamesProcessor(nameIX), amountIx);
    }

    /**
     * Static factory to create a new processor
     * for all Account Receivable (trade, others,
     * third parties and related parties).
     *
     * @return a new processor for Accounts Receivable.
     */
    @Contract(" -> new")
    static @NotNull PDTFieldProcessor receivable() {
        return new PDTFieldProcessor(3, 5);
    }

    /**
     * Static factory to create a new processor
     * for Account Doubtful entries.
     *
     * @return a new processort for Accounts doubtful.
     */
    @Contract(" -> new")
    static @NotNull PDTFieldProcessor doubtful() {
        return new PDTFieldProcessor(3, 9);
    }

    /**
     * Static factory to create a new processor
     * for Trade Accounts Payable.
     *
     * @return trade accounts payable processor.
     */
    @Contract(" -> new")
    static @NotNull PDTFieldProcessor payableTrade() {
        return new PDTFieldProcessor(4, 5);
    }

    /**
     * Static factory to create a new processor
     * for Other Accounts Payable.
     *
     * @return other accounts payable processor.
     */
    @Contract(" -> new")
    static @NotNull PDTFieldProcessor payableOthers() {
        return new PDTFieldProcessor(4, 6);
    }

    @Override
    public @NotNull PDTField apply(@NotNull List<CellData> cellData) {
        var r = new PDTField();
        r.parseDoiType(cellData.get(0).getFormattedValue());
        r.doiNum = cellData.get(2).getFormattedValue();
        onName.accept(cellData, r);
        r.amount = BigDecimal.valueOf(cellData.get(amountIx).getEffectiveValue().getNumberValue());
        return r;
    }
}
