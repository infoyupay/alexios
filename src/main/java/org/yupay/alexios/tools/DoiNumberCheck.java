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

import com.google.api.services.sheets.v4.model.RowData;
import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import org.yupay.alexios.google.GoogleUtils;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Process the check of doi numbers.
 *
 * @author InfoYupay SACS
 * @version 1.0
 */
public class DoiNumberCheck {
    /**
     * Checks doi numbers from LE0300 draft stored in a google sheetName.
     *
     * @param spreadsheet the sheetName object.
     * @return a map containing the failed checks by sheet.
     */
    public Map<String, List<DoiChallenger>> processLE03(Spreadsheet spreadsheet) {
        return processSpreadsheet(spreadsheet, List.of(
                new DoiNumberCheckRequest("030300", 0, 2, 5),
                new DoiNumberCheckRequest("030400", 0, 2, 5),
                new DoiNumberCheckRequest("030500", 0, 2, 5),
                new DoiNumberCheckRequest("030600", 0, 2, 5),
                new DoiNumberCheckRequest("031100", 1, 3, 5),
                new DoiNumberCheckRequest("031200", 0, 2, 5),
                new DoiNumberCheckRequest("031300", 0, 2, 5),
                new DoiNumberCheckRequest("031602", 0, 2, 5)
        ));
    }

    /**
     * Processes a sheetName for DOI check. It'll process a request for each sheet in sheetName
     * that is in the requests list.
     *
     * @param spreadsheet the sheetName object.
     * @param requests    the list of request objects.
     * @return a map with all requests solved.
     */
    public Map<String, List<DoiChallenger>> processSpreadsheet(
            Spreadsheet spreadsheet,
            @NotNull List<DoiNumberCheckRequest> requests) {
        var r = new HashMap<String, List<DoiChallenger>>();
        requests
                .forEach(x
                        -> r.put(x.sheetName(), processRequest(spreadsheet, x)));
        return r;
    }

    /**
     * Processes a single sheet for DOI num check.
     *
     * @param spreadsheet the sheetName object.
     * @param request     the request for a given worksheet.
     * @return the DOI challenger objects.
     */
    public List<DoiChallenger> processRequest(Spreadsheet spreadsheet, @NotNull DoiNumberCheckRequest request) {
        return processSheet(
                GoogleUtils.firstSheetByName(request.sheetName(), spreadsheet),
                request.doiTypeIndex(),
                request.doiNumberIndex(),
                request.headerSize());
    }

    /**
     * Processes a single sheet for DOI num check, without a request.
     *
     * @param sheet          the sheet object.
     * @param doiTypeIndex   doi type column index.
     * @param doiNumberIndex doi number column index.
     * @param headerSize     header size to skip rows.
     * @return list with failed challenges.
     */
    public List<DoiChallenger> processSheet(
            Sheet sheet,
            int doiTypeIndex,
            int doiNumberIndex,
            int headerSize) {
        var info = GoogleUtils.infoFlag(sheet);
        if (!info) return List.of();
        var count = new AtomicInteger(headerSize);
        return sheet.getData().getFirst().getRowData()
                .stream()
                .skip(headerSize)
                .map(RowData::getValues)
                .filter(GoogleUtils.ignoreBlank())
                .map(rw -> new DoiChallenger(
                        rw.get(doiTypeIndex).getFormattedValue(),
                        rw.get(doiNumberIndex).getFormattedValue(),
                        count.incrementAndGet()))
                .filter(DoiChallenger::challengeFailed)
                .toList();
    }
}
