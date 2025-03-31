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

package org.yupay.alexios.api.impl.balances;

import com.google.api.services.sheets.v4.model.GridData;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import static org.yupay.alexios.google.GoogleUtils.stringAt;

/**
 * Parameters to process LE0300 books.
 *
 * @param day        the last day of tax period as String, %02d. (B8 cell).
 * @param month      the last month of tax period as String, %02d. (B7 cell).
 * @param oportunity the oportunity flag as String, %02d. (B9 cell).
 * @param opsFlag    the operations flag as String, %d. (B16 cell).
 * @param ruc        the taxpayer id (RUC number) as String. (B5 cell).
 * @param year       the year of the tax period as String, %04d (uuuu). (B6 cell).
 * @version 1.0
 */
record Params03(String ruc,
                String year,
                String month,
                String day,
                String oportunity,
                String opsFlag) {
    /**
     * Creates an instance extracting data from a grid data.
     *
     * @param data a grid data (Google Spreadsheets API).
     */
    Params03(GridData data) {
        this(
                stringAt(data, 4, 1),
                stringAt(data, 5, 1),
                stringAt(data, 6, 1),
                stringAt(data, 7, 1),
                stringAt(data, 8, 1),
                stringAt(data, 15, 1)
        );
    }

    /**
     * Constructs the period ID out of {@link #year}, {@link #month} and {@link #day} params.
     *
     * @return the period ID which is yyyyMMdd
     */
    @Contract(pure = true)
    public @NotNull String periodID() {
        return "%s%s%s".formatted(year, month, day);
    }

    /**
     * Compiles the file name taking into account these parameters.
     *
     * @param bookID the book ID.
     * @param info   information flag.
     * @return file name.
     */
    @Contract(pure = true)
    public @NotNull String compileFile(String bookID, boolean info) {
        return compileFile(bookID, info, "txt");
    }

    /**
     * Compiles the file name taking into account these parameters.
     *
     * @param bookID    the book ID.
     * @param info      information flag.
     * @param extension the extension at the end of file.
     * @return file name.
     */
    @Contract(pure = true)
    public @NotNull String compileFile(String bookID, boolean info, String extension) {
        return "LE%s%s%s%s%s%s%s%s11.%s".formatted(
                ruc,
                year,
                month,
                day,
                bookID,
                oportunity,
                opsFlag,
                info ? "1" : "0",
                extension);
    }
}
