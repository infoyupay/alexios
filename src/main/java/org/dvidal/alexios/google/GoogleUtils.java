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
import com.google.api.services.sheets.v4.model.CellData;
import com.google.api.services.sheets.v4.model.Spreadsheet;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
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
    public static Spreadsheet getSheet(final String spreadSheetID) throws IOException, GeneralSecurityException {
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
        var r = cell.getEffectiveValue().getNumberValue();
        return r == null ? 0 : r.intValue();
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

}
