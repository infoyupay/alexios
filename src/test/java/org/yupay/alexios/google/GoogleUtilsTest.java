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

package org.yupay.alexios.google;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.SheetProperties;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class GoogleUtilsTest {
    static final String SHEET_ID = "1f4IlMSLRutavYJeyEx0G4jy2OdnSNLt8iQ8rl6HcBXg";

    //Sample file: 1f4IlMSLRutavYJeyEx0G4jy2OdnSNLt8iQ8rl6HcBXg
    @Test
    void credentialAndReadTest() throws Exception {
        final var transport = GoogleNetHttpTransport.newTrustedTransport();
        final var range = "070000!B5";
        var service = new Sheets.Builder(transport, GoogleUtils.JSON_FACTORY, GoogleUtils.getCredentialsSheets(transport))
                .setApplicationName(GoogleUtils.APP_NAME)
                .build();
        var response = service.spreadsheets().values()
                .get(SHEET_ID, range)
                .execute();

        var values = response.getValues();
        Assertions.assertNotNull(values);
        Assertions.assertFalse(values.isEmpty());
        values.forEach(l -> l.forEach(System.out::println));
    }

    @Test
    void spreadSheetTest() throws Exception {
        var sheet = GoogleUtils.getSpreadsheet(SHEET_ID);
        sheet.getSheets()
                .stream()
                .map(Sheet::getProperties)
                .map(SheetProperties::getTitle)
                .forEach(System.out::println);
    }
}
