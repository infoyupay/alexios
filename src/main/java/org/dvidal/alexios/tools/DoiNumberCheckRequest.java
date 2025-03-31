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

package org.dvidal.alexios.tools;

/**
 * Record to hold requests information for a given worksheet.
 *
 * @param sheetName      the name of worksheet inside the spreadsheet.
 * @param doiTypeIndex   column index for doi type.
 * @param doiNumberIndex column index for doi number.
 * @param headerSize     header size to skip rows.
 * @author InfoYupay SACS
 * @version 1.0
 */
public record DoiNumberCheckRequest(String sheetName,
                                    int doiTypeIndex,
                                    int doiNumberIndex,
                                    int headerSize) {
}
