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

package org.dvidal.alexios.google;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.*;

import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Utility interface with useful constants for Google API.
 *
 * @version 1.0
 */
public class GoogleUtils {
    /**
     * The application name, which is alexios.
     */
    public static final String APP_NAME = "alexios";
    /**
     * The default Json Factory, which is Gson.
     */
    public static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    /**
     * The system path to store tokens.
     */
    public static final String TOKENS_PATH = "tokens";
    /**
     * A singleton scope to read spreadsheets.
     */
    public static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS_READONLY);
    /**
     * A resource path to credentials.json file.
     */
    public static final String CREDENTIALS_PATH = "/credentials.json";

    /**
     * Utility method to create credentials for Google API.
     *
     * @param transport the GoogleAuthorizationCodeFlow http transport.
     * @return a credential to use in the app.
     * @throws IOException if the credentials resources cannot be loaded.
     */
    public static Credential getCredentials(final NetHttpTransport transport) throws IOException {
        var is = GoogleUtils.class.getResourceAsStream(CREDENTIALS_PATH);
        if (is == null)
            throw new FileNotFoundException("Resource not found " + CREDENTIALS_PATH);
        var secrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(is));
        var flow = new GoogleAuthorizationCodeFlow.Builder(transport, JSON_FACTORY, secrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new File(TOKENS_PATH)))
                .setAccessType("offline")
                .build();
        var receiver = new LocalServerReceiver.Builder()
                .setPort(8888)
                .build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    /**
     * Retrieves a Spreadsheet using the spreadSheet ID value.
     *
     * @param spreadSheetID the spreadsheet ID value.
     * @return requested spreadsheet.
     * @throws IOException              if thrown by getCredentials.
     * @throws GeneralSecurityException if thrown by GoogleNetHttpTransport.newTrustedTransport.
     */
    public static Spreadsheet getSpreadsheet(final String spreadSheetID) throws IOException, GeneralSecurityException {
        final var transport = GoogleNetHttpTransport.newTrustedTransport();
        final var service = new Sheets
                .Builder(transport, JSON_FACTORY, getCredentials(transport))
                .setApplicationName(APP_NAME)
                .build();
        return service.spreadsheets().get(spreadSheetID).setIncludeGridData(true).execute();
    }

    /**
     * Utility method to extract a double value from a given cell.
     *
     * @param cell a given cell.
     * @return a double value from cell, or 0.0 if null.
     */
    public static double doubleFromCell(CellData cell) {
        var r = cell.getEffectiveValue().getNumberValue();
        return r == null ? 0.0 : r;
    }

    /**
     * Utility method to extract an int value from a given cell.
     *
     * @param cell a given cell.
     * @return an int value from cell, or 09 if null.
     */
    public static int intFromCell(CellData cell) {
        return Optional.ofNullable(cell.getEffectiveValue())
                .map(ExtendedValue::getNumberValue)
                .map(Double::intValue)
                .orElse(0);
    }

    public static BigDecimal decimalFrom(CellData cell) {
        return Optional.ofNullable(cell.getEffectiveValue())
                .map(ExtendedValue::getNumberValue)
                .map(BigDecimal::valueOf)
                .orElse(BigDecimal.ZERO);
    }

    /**
     * Utility method to extract a date as String from a given cell.
     * It's expected that date cells are formatted as dd-MM-uuuu
     *
     * @param cell the given cell.
     * @return a date as string in format dd/MM/uuuu
     */
    public static String fromDateCell(CellData cell) {
        var dt = cell.getFormattedValue();
        if (dt == null || dt.isBlank()) {
            return "00/00/0000";
        } else {
            return "%s/%s/%s".formatted(dt.substring(0, 2), dt.substring(3, 5), dt.substring(6));
        }
    }

    /**
     * Utility method to create a filter in order to ignore all
     * rows starting with an empty cell.
     *
     * @return a useful predicate.
     */
    public static Predicate<List<CellData>> ignoreBlank() {
        return ls -> {
            if (ls == null || ls.isEmpty()) return false;
            var cell = ls.get(0);
            if (cell == null) return false;
            var txt = cell.getFormattedValue();
            return !(txt == null || txt.isBlank());
        };
    }

    /**
     * Utility method to solve a code pattern in which I have to
     * read doubles from sucessive columns and put them into
     * some array items, in such a way that the cell data column
     * index is one behind array item index. Ie:
     * <pre>
     * {@code
     * line[10] = "%.2f".formatted(doubleFromCell(row.get(9)));
     * line[11] = "%.2f".formatted(doubleFromCell(row.get(10)));
     * line[12] = "%.2f".formatted(doubleFromCell(row.get(11)));
     * line[13] = "%.2f".formatted(doubleFromCell(row.get(12)));
     * line[14] = "%.2f".formatted(doubleFromCell(row.get(13)));
     * }
     * </pre>
     * This code pattern is repeated in many book processors.
     *
     * @param target the target array of strings where read data should be put.
     * @param from   the begining (inclusive) index at which put data.
     * @param until  the end (inclusive) index at which put data.
     * @param row    the google sheet row represented as a List of CellData.
     */
    public static void readDoubleBehind(String[] target, int from, int until, List<CellData> row) {
        for (var i = from; i <= until; i++) {
            target[i] = "%.2f".formatted(doubleFromCell(row.get(i - 1)));
        }
    }

    /**
     * Utility method to solve a code pattern in which I have to
     * read doubles from sucessive columns and put them into
     * some array items, in such a way that the cell data column
     * index is one ahead array item index. Ie:
     * <pre>
     * {@code
     * line[10] = "%.2f".formatted(doubleFromCell(row.get(11)));
     * line[11] = "%.2f".formatted(doubleFromCell(row.get(12)));
     * line[12] = "%.2f".formatted(doubleFromCell(row.get(13)));
     * line[13] = "%.2f".formatted(doubleFromCell(row.get(14)));
     * line[14] = "%.2f".formatted(doubleFromCell(row.get(15)));
     * }
     * </pre>
     * This code pattern is repeated in many book processors.
     *
     * @param target the target array of strings where read data should be put.
     * @param from   the begining (inclusive) index at which put data.
     * @param until  the end (inclusive) index at which put data.
     * @param row    the google sheet row represented as a List of CellData.
     */
    public static void readDoubleAhead(String[] target, int from, int until, List<CellData> row) {
        for (var i = from; i <= until; i++) {
            target[i] = "%.2f".formatted(doubleFromCell(row.get(i + 1)));
        }
    }

    /**
     * Utility method to write the data from a worksheet and mapped
     * to PLE tuples, into a plain txt file.
     *
     * @param worksheet  the worksheet.
     * @param skipHeader the header rows count to skip.
     * @param fileName   the file name where to write.
     * @param path       the output directory path (parent of target file).
     * @param converter  The converter to extract PLE tuples from a worksheet.
     * @throws IOException if output fails.
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void exportFile(Sheet worksheet,
                                  long skipHeader,
                                  String fileName,
                                  File path,
                                  Function<List<CellData>, String> converter) throws IOException {
        if (!path.exists()) path.mkdirs();
        var output = new File(path, fileName);
        if (output.exists()) output.delete();
        //Check info flag
        if (fileName.charAt(30) == '0') {
            output.createNewFile();
        } else {
            //Exports using the provided converter.
            try (var fos = new FileOutputStream(output, false);
                 var ps = new PrintStream(fos, true, StandardCharsets.UTF_8)) {
                worksheet.getData().get(0).getRowData()
                        .stream()
                        .skip(skipHeader)
                        .map(RowData::getValues)
                        .filter(ignoreBlank())
                        .map(converter)
                        .forEachOrdered(ps::print);
            }
        }
    }

    /**
     * Utility method to detect the empty flag at cell A1.
     *
     * @param worksheet the worksheet.
     * @return true if A1 is true.
     */
    public static boolean infoFlag(Sheet worksheet) {
        return worksheet.getData()
                .get(0)
                .getRowData()
                .stream()
                .map(RowData::getValues)
                .filter(r -> !r.isEmpty())
                .map(r -> r.get(0))
                .map(CellData::getEffectiveValue)
                .map(ExtendedValue::getBoolValue)
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("Cannot find A1 cell in worksheet."));
    }

    public static GridData firstGridByName(String sheet, Spreadsheet spreadsheet) {
        return firstGridAs(sheet, spreadsheet)
                .orElseThrow(() -> noSheet(sheet));
    }

    public static Optional<GridData> firstGridAs(String sheet, Spreadsheet spreadsheet) {
        return firstSheetAs(sheet, spreadsheet)
                .map(Sheet::getData)
                .map(l -> l.get(0));
    }

    public static Optional<Sheet> firstSheetAs(String sheet, Spreadsheet spreadsheet) {
        return spreadsheet.getSheets().stream()
                .filter(s -> Objects.equals(sheet, s.getProperties().getTitle()))
                .findFirst();
    }

    public static Sheet firstSheetByName(String sheet, Spreadsheet spreadsheet) {
        return firstSheetAs(sheet, spreadsheet)
                .orElseThrow(() -> noSheet(sheet));
    }

    public static String stringAt(GridData data, int row, int column) {
        return Optional.ofNullable(data.getRowData())
                .map(r -> r.get(row))
                .map(RowData::getValues)
                .map(l -> l.get(column))
                .map(CellData::getFormattedValue)
                .orElseThrow(() -> noCell(row, column));
    }

    public static BigDecimal decimalAt(GridData data, int row, int column) {
        return Optional.ofNullable(data.getRowData())
                .map(r -> r.get(row))
                .map(RowData::getValues)
                .map(l -> l.get(column))
                .map(CellData::getEffectiveValue)
                .map(ExtendedValue::getNumberValue)
                .map(BigDecimal::valueOf)
                .orElse(BigDecimal.ZERO);
    }

    public static IllegalArgumentException noCell(int row, int column) {
        return new IllegalArgumentException("Cannot find cell at row %d; column %d."
                .formatted(row, column));
    }

    public static IllegalArgumentException noSheet(String sheetName) {
        return new IllegalArgumentException("Cannot find sheet with name: " + sheetName);
    }
}
