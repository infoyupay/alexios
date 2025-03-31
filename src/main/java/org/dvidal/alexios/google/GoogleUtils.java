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
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Utility interface with useful constants for Google API.
 *
 * @version 1.0
 */
public class GoogleUtils {
    /**
     * Decimal format symbols for PLE specs.
     */
    public static final DecimalFormatSymbols PLE_SYM = new DecimalFormatSymbols() {

        @Override
        public char getDecimalSeparator() {
            return '.';
        }

        @Override
        public char getMonetaryDecimalSeparator() {
            return '.';
        }
    };
    /**
     * General decimal format for PLE specs.
     */
    public static final DecimalFormat PLE_FMT = new DecimalFormat("###0.00", PLE_SYM);
    /**
     * Decimal format to use in LE 1602, field 7 specs.
     */
    public static final DecimalFormat PLE_1602_7_FMT = new DecimalFormat("###0.00000000", PLE_SYM);
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
     * A scope to read spreadsheets with the following scopes:
     * <ol>
     *     <li>{@link SheetsScopes#SPREADSHEETS_READONLY}</li>
     *     <li>{@link SheetsScopes#DRIVE_READONLY}</li>
     * </ol>
     */
    public static final List<String> SCOPES = List.of(SheetsScopes.SPREADSHEETS_READONLY, DriveScopes.DRIVE_READONLY);
    /**
     * A resource path to credentials.json file.
     */
    public static final String CREDENTIALS_PATH = "/credentials.json";

    static {
        PLE_FMT.setParseBigDecimal(true);
        PLE_1602_7_FMT.setParseBigDecimal(true);
    }

    /**
     * Utility method to create credentials for Google API.
     *
     * @param transport the GoogleAuthorizationCodeFlow http transport.
     * @return a credential to use in the app.
     * @throws IOException if the credentials resources cannot be loaded.
     */
    public static Credential getCredentialsSheets(final NetHttpTransport transport) throws IOException {
        return getCredentials(transport, SCOPES);
    }

    /**
     * Utility method to create credentials for Google API.
     * It shall use {@link #getCredentials(NetHttpTransport, List)} with the scopes {@link #SCOPES}
     *
     * @param transport the GoogleAuthorizationCodeFlow http transport.
     * @return a credential to use in the app.
     * @throws IOException if the credentials resources cannot be loaded.
     */
    public static Credential getCredentialsDrive(final NetHttpTransport transport) throws IOException {
        return getCredentials(transport, SCOPES);
    }

    /**
     * Utility method to create credentials for Google API.
     *
     * @param transport the GoogleAuthorizationCodeFlow http transport.
     * @param scopes    the scopes for the credential.
     * @return a credential to use in the app.
     * @throws IOException if the credentials resources cannot be loaded.
     */
    public static Credential getCredentials(final NetHttpTransport transport, final List<String> scopes) throws IOException {
        var is = GoogleUtils.class.getResourceAsStream(CREDENTIALS_PATH);
        if (is == null)
            throw new FileNotFoundException("Resource not found " + CREDENTIALS_PATH);
        var secrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(is));
        var flow = new GoogleAuthorizationCodeFlow.Builder(transport, JSON_FACTORY, secrets, scopes)
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
     * @param spreadSheetID the sheetName ID value.
     * @return requested sheetName.
     * @throws IOException              if thrown by getCredentials.
     * @throws GeneralSecurityException if thrown by GoogleNetHttpTransport.newTrustedTransport.
     */
    public static Spreadsheet getSpreadsheet(final String spreadSheetID) throws IOException, GeneralSecurityException {
        final var transport = GoogleNetHttpTransport.newTrustedTransport();
        final var service = new Sheets
                .Builder(transport, JSON_FACTORY, getCredentialsSheets(transport))
                .setApplicationName(APP_NAME)
                .build();
        return service.spreadsheets().get(spreadSheetID).setIncludeGridData(true).execute();
    }

    /**
     * Downloads a google drive file by ID into a target path.
     *
     * @param driveID the google drive ID.
     * @param target  the target path.
     * @throws IOException              if cannot write.
     * @throws GeneralSecurityException if security manager throws it.
     */
    public static void downloadDriveFile(final String driveID, final Path target) throws IOException, GeneralSecurityException {
        final var transport = GoogleNetHttpTransport.newTrustedTransport();
        final var service = new Drive.Builder(transport, JSON_FACTORY, getCredentialsDrive(transport))
                .setApplicationName(APP_NAME)
                .build();

        try (var fos = Files.newOutputStream(target)) {
            service.files().get(driveID).executeMediaAndDownloadTo(fos);
        }
    }

    /**
     * Utility method to extract a double value from a given cell.
     *
     * @param cell a given cell.
     * @return a double value from cell, or 0.0 if null.
     */
    public static double doubleFromCell(@NotNull CellData cell) {
        return Optional.ofNullable(cell.getEffectiveValue())
                .map(ExtendedValue::getNumberValue)
                .orElse(0D);
    }

    /**
     * Utility method to extract an int value from a given cell.
     *
     * @param cell a given cell.
     * @return an int value from cell, or 09 if null.
     */
    public static int intFromCell(@NotNull CellData cell) {
        return Optional.ofNullable(cell.getEffectiveValue())
                .map(ExtendedValue::getNumberValue)
                .map(Double::intValue)
                .orElse(0);
    }

    /**
     * Utility method to safely extract a BigDecimal from a CellData object
     * and format according to PLE specification (###0.00).
     * If the cell is null, or empty at some point, 0 will be returned.
     * Otherwise, will extract the number value and build a BigDecimal from it
     * using {@link BigDecimal#valueOf(double)}, then will format to String
     * using the constant {@link #PLE_FMT}. If the cell doesn't contain
     * a number value, "0.00" will be returned as well.
     *
     * @param cell the cell object.
     * @return text from cell value as PLE specs or empty String.
     */
    public static String decimalText(CellData cell) {
        return decimalIn(cell)
                .map(PLE_FMT::format)
                .orElse("0.00");
    }

    /**
     * Convenient method to format decimals for LE1602, field 7 specs.
     * Extracts data from a CellData object representing a cell, if it contains decimal data,
     * will format using the {@link #PLE_1602_7_FMT} decimal format.
     *
     * @param cell the cell object.
     * @return formatted decimal value, if no value is present "0.00000000"
     */
    public static String decimalText1602(CellData cell) {
        return decimalIn(cell)
                .map(PLE_1602_7_FMT::format)
                .orElse("0.00000000");
    }

    /**
     * Convenient method to extract a BigDecimal value from a cell object.
     *
     * @param cell the cell object.
     * @return an optional containing the number value of the cell object,
     * converted to BigDecimal, if no number value is found it'll be empty.
     */
    public static Optional<BigDecimal> decimalIn(CellData cell) {
        return Optional.ofNullable(cell)
                .map(CellData::getEffectiveValue)
                .map(ExtendedValue::getNumberValue)
                .map(BigDecimal::valueOf);
    }

    /**
     * Utility method to extract a date as String from a given cell.
     * It's expected that date cells are formatted as dd-MM-uuuu
     *
     * @param cell the given cell.
     * @return a date as string in format dd/MM/uuuu
     */
    public static @NotNull String fromDateCell(@NotNull CellData cell) {
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
    @Contract(pure = true)
    public static @NotNull Predicate<List<CellData>> ignoreBlank() {
        return ls -> {
            if (ls == null || ls.isEmpty()) return false;
            var cell = ls.getFirst();
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
     * @param rowFilter  a filter to ignore rows that doesn't pass validation.
     * @throws IOException if output fails.
     */
    public static void exportFile(Sheet worksheet,
                                  long skipHeader,
                                  String fileName,
                                  @NotNull Path path,
                                  Function<List<CellData>, String> converter,
                                  @NotNull Predicate<List<CellData>> rowFilter) throws IOException {
        //If the target output folder doesn't exist, create.
        if (!Files.exists(path)) Files.createDirectories(path);
        //Resolve file name within path.
        var output = path.resolve(fileName);
        //Clean the file.
        recreateFile(output);
        //Check info flag
        if (fileName.charAt(30) != '0') {
            //Exports using the provided converter.
            try (var os = Files.newOutputStream(output);
                 var ps = new PrintStream(os, true, StandardCharsets.UTF_8)) {
                worksheet.getData().getFirst().getRowData()
                        .stream()
                        .skip(skipHeader)
                        .map(RowData::getValues)
                        .filter(rowFilter)
                        .map(converter)
                        .forEachOrdered(ps::print);
            }
        }
    }

    /**
     * Utility method to write the data from a worksheet and mapped
     * to PLE tuples, into a plain txt file. This is the default, which
     * ignores blank rows using {@link #ignoreBlank()} filter.
     *
     * @param worksheet  the worksheet.
     * @param skipHeader the header rows count to skip.
     * @param fileName   the file name where to write.
     * @param path       the output directory path (parent of target file).
     * @param converter  The converter to extract PLE tuples from a worksheet.
     * @throws IOException if output fails.
     */
    public static void exportFile(Sheet worksheet,
                                  long skipHeader,
                                  String fileName,
                                  @NotNull Path path,
                                  Function<List<CellData>, String> converter) throws IOException {
        exportFile(worksheet, skipHeader, fileName, path, converter, ignoreBlank());
    }

    /**
     * Utility method to check if a file exists and delete.
     * The, even if file didn't exist, will "touch" (create) it.
     *
     * @param aFile the file to check.
     * @throws IOException if cannot delete or create.
     */
    public static void recreateFile(Path aFile) throws IOException {
        Files.write(aFile, new byte[0]);
    }

    /**
     * Utility method to detect the empty flag at cell A1.
     *
     * @param worksheet the worksheet.
     * @return true if A1 is true.
     */
    public static boolean infoFlag(@NotNull Sheet worksheet) {
        return worksheet.getData()
                .getFirst()
                .getRowData()
                .stream()
                .map(RowData::getValues)
                .filter(r -> !r.isEmpty())
                .map(List::getFirst)
                .map(CellData::getEffectiveValue)
                .map(ExtendedValue::getBoolValue)
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("Cannot find A1 cell in worksheet."));
    }

    /**
     * Get the first grid (sheet) object with given name in the sheetName.
     *
     * @param sheet       the required sheet name.
     * @param spreadsheet the sheetName object.
     * @return the grid data of given sheet name.
     * @throws IllegalArgumentException if there's no sheet with given sheet name.
     */
    public static GridData firstGridByName(String sheet, Spreadsheet spreadsheet) {
        return firstGridAs(sheet, spreadsheet)
                .orElseThrow(() -> noSheet(sheet));
    }

    /**
     * Get the first grid (sheet) object with given name in the sheetName as an Optional.
     *
     * @param sheet       the required sheet name.
     * @param spreadsheet the sheetName object.
     * @return the grid data of given sheet name.
     * @throws IllegalArgumentException if there's no sheet with given sheet name.
     */
    public static Optional<GridData> firstGridAs(String sheet, Spreadsheet spreadsheet) {
        return firstSheetAs(sheet, spreadsheet)
                .map(Sheet::getData)
                .map(List::getFirst);
    }

    /**
     * Get the first sheet object with given name as a worksheet Optional.
     *
     * @param sheet       the required name.
     * @param spreadsheet the sheetName object.
     * @return an optional with required sheet.
     */
    public static @NotNull Optional<Sheet> firstSheetAs(String sheet, @NotNull Spreadsheet spreadsheet) {
        return spreadsheet.getSheets().stream()
                .filter(s -> Objects.equals(sheet, s.getProperties().getTitle()))
                .findFirst();
    }

    /**
     * Get the first sheet object with given name as a worksheet.
     *
     * @param sheet       the required name.
     * @param spreadsheet the sheetName object.
     * @return an optional with required sheet.
     * @throws IllegalArgumentException if no sheet with given name is found.
     */
    public static Sheet firstSheetByName(String sheet, Spreadsheet spreadsheet) {
        return firstSheetAs(sheet, spreadsheet)
                .orElseThrow(() -> noSheet(sheet));
    }

    /**
     * Extracts the formatted value from a given cell (by coordinates).
     *
     * @param data   the grid of data.
     * @param row    the row index.
     * @param column the column index.
     * @return the required value as formattedValue, or "" if no value is found.
     */
    @Contract("_, _, _ -> !null")
    public static String stringAt(@NotNull GridData data, int row, int column) {
        return Optional.ofNullable(data.getRowData())
                .map(r -> r.get(row))
                .map(RowData::getValues)
                .map(l -> l.get(column))
                .map(CellData::getFormattedValue)
                .orElse("");
    }

    /**
     * Extracts the number value from a cell (by coordinates)
     * and returns it as a BigDecimal object.
     *
     * @param data   the grid of data.
     * @param row    the cell row index.
     * @param column the cell column index.
     * @return the bigdecimal from the cell value, or {@link BigDecimal#ZERO} if no value is found.
     */
    public static BigDecimal decimalAt(@NotNull GridData data, int row, int column) {
        return Optional.ofNullable(data.getRowData())
                .map(r -> r.get(row))
                .map(RowData::getValues)
                .map(l -> l.get(column))
                .map(CellData::getEffectiveValue)
                .map(ExtendedValue::getNumberValue)
                .map(BigDecimal::valueOf)
                .orElse(BigDecimal.ZERO);
    }

    /**
     * Extracts the formatted value of a cell as a numeric text copying only digits.
     *
     * @param cell the cell object.
     * @return only digits String value.
     */
    public static String numericText(CellData cell) {
        return Optional.ofNullable(cell)
                .map(CellData::getFormattedValue)
                .stream().flatMapToInt(String::codePoints)
                .filter(Character::isDigit)
                .mapToObj(Character::toString)
                .collect(Collectors.joining());
    }

    /**
     * Extracts text from the formatted value of a cell, and makes the value safe
     * for further operations replacing / by -
     *
     * @param data   the data row.
     * @param column the column index.
     * @return the safe text value or - if no value is found.
     */
    @Contract("_, _ -> !null")
    public static String safeText(@NotNull List<CellData> data, int column) {
        return data
                .stream()
                .skip(column)
                .findFirst()
                .map(CellData::getFormattedValue)
                .map(s -> s.replaceAll("/", "-"))
                .orElse("-");
    }

    /**
     * Convenient method to create IllegalArgumentExcpetion objects to throw
     * when searching for a sheet with a given name but not finding it.
     *
     * @param sheetName the not found sheet name.
     * @return a ready to throw exception.
     */
    @Contract("_ -> new")
    public static @NotNull IllegalArgumentException noSheet(String sheetName) {
        return new IllegalArgumentException("Cannot find sheet with name: " + sheetName);
    }
}
