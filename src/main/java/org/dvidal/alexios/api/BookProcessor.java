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

package org.dvidal.alexios.api;

import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.Spreadsheet;

import java.io.File;

/**
 * The book processor is the responsible for creating the output TXT
 * from a google spreadsheet object.
 *
 * @version 1.0
 */
public interface BookProcessor {
    /**
     * The book firendly title to show to user.
     *
     * @return the book title.
     */
    String title();

    /**
     * Processes a spreadsheet, transforming the google data
     * into plain TXT as specified by SUNAT-PLE.
     *
     * @param spreadsheet the spreadsheet object.
     * @param target      the directory where files should be saved.
     * @throws Exception if implementation requires so.
     */
    void processSheet(Spreadsheet spreadsheet, File target) throws Exception;

    /**
     * Convenient method to read the boolean value of A1.
     * This is useful to read information flag.
     *
     * @param worksheet the sheet where to find.
     * @return true if checked, false otherwise.
     */
    default boolean readInfoFlag(Sheet worksheet) {
        return worksheet
                .getData()
                .get(0)
                .getRowData()
                .get(0)
                .getValues()
                .get(0)
                .getEffectiveValue()
                .getBoolValue();
    }
}
