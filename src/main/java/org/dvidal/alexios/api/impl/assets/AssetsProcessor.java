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

package org.dvidal.alexios.api.impl.assets;

import com.google.api.services.sheets.v4.model.CellData;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import org.dvidal.alexios.api.BookProcessor;
import org.dvidal.alexios.api.PLEBookNameBuilder;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

import static org.dvidal.alexios.google.GoogleUtils.*;

/**
 * Implementation for LE070000 - Assets book.
 * See PLE specification 070000.
 *
 * @author InfoYupay SACS
 * @version 1.0
 */
public class AssetsProcessor implements BookProcessor {
    private String ruc;
    private String year;
    private String period;
    private String opsFlag;

    @Override
    public String title() {
        return "LE070000 - Registro de Activos Fijos.";
    }

    @Override
    public void processSheet(Spreadsheet spreadsheet, Path target) throws Exception {
        //Retrieve parameters from the 070000 spreadsheet.
        retrieveParameters(spreadsheet);
        //Iterate thru spreadsheets.
        for (var worksheet : spreadsheet.getSheets()) {
            //Check information flag (A1).
            var infoFlag = readInfoFlag(worksheet);
            //Check worksheet name.
            switch (worksheet.getProperties().getTitle()) {
                //0701 book: Fixed assets.
                case "070100" -> exportFile(worksheet, 4,
                        buildFnam("070100", infoFlag),
                        target,
                        new LE0701Converter());
                //0703 book: exchange rate difference.
                case "070300" -> exportFile(worksheet, 3,
                        buildFnam("070300", infoFlag),
                        target,
                        new LE0703Converter());
                //0704 book: leased assets.
                case "070400" -> exportFile(worksheet, 3,
                        buildFnam("070400", infoFlag),
                        target,
                        new LE0704Converter());
            }
        }
    }

    /**
     * Inner method to extract book parameters.
     * In the first sheet named 070000 of the workbook shall be ruc, year, period and opsFlag parameters.
     *
     * @param spreadsheet the spreadsheet object.
     * @throws IllegalArgumentException if there's no "070000" sheet.
     */
    private void retrieveParameters(@NotNull Spreadsheet spreadsheet)
            throws IllegalArgumentException {
        var ss = spreadsheet.getSheets().stream()
                .filter(x -> Objects.equals(x.getProperties().getTitle(), "070000"))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("Cannot find worksheet 070000."));
        var rows = ss.getData().getFirst().getRowData();
        ruc = rows.get(4).getValues().get(1).getFormattedValue();
        year = rows.get(5).getValues().get(1).getFormattedValue();
        period = year + "0000";
        opsFlag = rows.get(6).getValues().get(1).getFormattedValue();
    }

    /**
     * Convenient method to generate output filenames.
     *
     * @param bookID   the PLE book ID.
     * @param infoFlag the flag of information (true= with information, false= empty).
     * @return an output file name.
     */
    private @NotNull String buildFnam(String bookID, boolean infoFlag) {
        return new PLEBookNameBuilder()
                .withBookID(bookID)
                .withEmpty(infoFlag)
                .withMonth("00")
                .withOpsFlag(opsFlag)
                .withRuc(ruc)
                .withYear(year)
                .build();

    }

    /**
     * Inner method to fill primary keys values. According to PLE specs, the first 3 fields are
     * primary key fields, composed by:
     * <li>
     *     <ul><b>0 - Period:</b> taxation period.</ul>
     *     <ul><b>1 - ID:</b> ID for the op. Since we are not generating from database, a random UUID is set.</ul>
     *     <ul><b>2 - Correlative:</b> Correlative number in format M000000000</ul>
     * </li>
     *
     * @param line        Strings array for each part of the line.
     * @param correlative the thread-safe correlative container.
     */
    private void fillPrimaryKey(String @NotNull [] line, @NotNull AtomicLong correlative) {
        line[0] = period;
        line[1] = UUID.randomUUID().toString();
        line[2] = "M%09d".formatted(correlative.incrementAndGet());
    }

    /**
     * Inner function to format the CellData of a row into a String
     * as specified by PLE - 0701 - Fixed assets. It'll contain windows
     * end of line (\r\n) since PLE system only accepts said end of line.
     *
     * @author InfoYupay SACS
     * @version 1.0
     */
    private class LE0701Converter implements Function<List<CellData>, String> {
        private final AtomicLong correlative = new AtomicLong(0L);

        @Override
        public @NotNull String apply(@NotNull List<CellData> cellData) {
            var line = new String[38];
            fillPrimaryKey(line, correlative);
            line[3] = cellData.get(0).getFormattedValue();
            line[4] = cellData.get(2).getFormattedValue();
            line[5] = cellData.get(4).getFormattedValue();
            line[6] = cellData.get(6).getFormattedValue();
            line[7] = cellData.get(8).getFormattedValue();
            line[8] = cellData.get(10).getFormattedValue();
            line[9] = cellData.get(11).getFormattedValue();
            line[10] = "%.40s".formatted(cellData.get(3).getFormattedValue());
            line[11] = "%.20s".formatted(Objects.requireNonNullElse(cellData.get(13).getFormattedValue(), "-"));
            line[12] = "%.20s".formatted(Objects.requireNonNullElse(cellData.get(14).getFormattedValue(), "-"));
            line[13] = "%.30s".formatted(Objects.requireNonNullElse(cellData.get(15).getFormattedValue(), "-"));
            line[14] = "%.2f".formatted(doubleFromCell(cellData.get(16)));
            line[15] = "%.2f".formatted(doubleFromCell(cellData.get(17)));
            line[16] = "%.2f".formatted(doubleFromCell(cellData.get(18)));
            line[17] = "%.2f".formatted(doubleFromCell(cellData.get(19)));
            line[18] = "%.2f".formatted(doubleFromCell(cellData.get(20)));
            line[19] = "%.2f".formatted(doubleFromCell(cellData.get(21)));
            line[20] = "%.2f".formatted(doubleFromCell(cellData.get(22)));
            line[21] = "%.2f".formatted(doubleFromCell(cellData.get(23)));
            line[22] = "%.2f".formatted(doubleFromCell(cellData.get(24)));
            line[23] = fromDateCell(cellData.get(25));
            line[24] = fromDateCell(cellData.get(26));
            line[25] = cellData.get(27).getFormattedValue();
            line[26] = cellData.get(29).getFormattedValue();
            line[27] = "%.2f".formatted(doubleFromCell(cellData.get(30)));
            line[28] = "%.2f".formatted(doubleFromCell(cellData.get(31)));
            line[29] = "%.2f".formatted(doubleFromCell(cellData.get(32)));
            line[30] = "%.2f".formatted(doubleFromCell(cellData.get(33)));
            line[31] = "%.2f".formatted(doubleFromCell(cellData.get(34)));
            line[32] = "%.2f".formatted(doubleFromCell(cellData.get(35)));
            line[33] = "%.2f".formatted(doubleFromCell(cellData.get(36)));
            line[34] = "%.2f".formatted(doubleFromCell(cellData.get(37)));
            line[35] = "%.2f".formatted(doubleFromCell(cellData.get(38)));
            line[36] = "1";
            line[37] = "\r\n";

            return String.join("|", line);
        }
    }

    /**
     * Inner function to format the CellData of a row into a String
     * as specified by PLE - 0703 - Exchange rate difference. It'll contain windows
     * end of line (\r\n) since PLE system only accepts said end of line.
     *
     * @author InfoYupay SACS
     * @version 1.0
     */
    private class LE0703Converter implements Function<List<CellData>, String> {
        private final AtomicLong correlative = new AtomicLong(0L);

        @Override
        public String apply(List<CellData> cellData) {
            var line = new String[16];
            fillPrimaryKey(line, correlative);
            line[3] = "9";
            line[4] = cellData.get(0).getFormattedValue();
            line[5] = fromDateCell(cellData.get(1));
            line[6] = "%.2f".formatted(doubleFromCell(cellData.get(2)));
            line[7] = "%.3f".formatted(doubleFromCell(cellData.get(3)));
            line[8] = "%.2f".formatted(doubleFromCell(cellData.get(4)));
            line[9] = "%.3f".formatted(doubleFromCell(cellData.get(5)));
            line[10] = "%.2f".formatted(doubleFromCell(cellData.get(6)));
            line[11] = "%.2f".formatted(doubleFromCell(cellData.get(7)));
            line[12] = "%.2f".formatted(doubleFromCell(cellData.get(8)));
            line[13] = "%.2f".formatted(doubleFromCell(cellData.get(9)));
            line[14] = "1";
            line[15] = "\r\n";

            return String.join("|", line);
        }
    }

    /**
     * Inner function to format the CellData of a row into a String
     * as specified by PLE - 0704 - Leased assets. It'll contain windows
     * end of line (\r\n) since PLE system only accepts said end of line.
     *
     * @author InfoYupay SACS
     * @version 1.0
     */
    private class LE0704Converter implements Function<List<CellData>, String> {
        private final AtomicLong correlative = new AtomicLong(0L);

        @Override
        public @NotNull String apply(@NotNull List<CellData> cellData) {
            var line = new String[12];
            fillPrimaryKey(line, correlative);
            line[3] = "9";
            line[4] = cellData.get(0).getFormattedValue();
            line[5] = fromDateCell(cellData.get(1));
            line[6] = cellData.get(2).getFormattedValue();
            line[7] = fromDateCell(cellData.get(4));
            line[8] = "%d".formatted(intFromCell(cellData.get(5)));
            line[9] = "%.2f".formatted(doubleFromCell(cellData.get(6)));
            line[10] = "1";
            line[11] = "\r\n";

            return String.join("|", line);
        }
    }


}
