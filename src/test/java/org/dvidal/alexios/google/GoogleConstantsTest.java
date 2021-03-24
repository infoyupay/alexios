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

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.services.sheets.v4.Sheets;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class GoogleConstantsTest {
    //Sample file: 1f4IlMSLRutavYJeyEx0G4jy2OdnSNLt8iQ8rl6HcBXg
    @Test
    void credentialAndReadTest() throws Exception {
        final var transport = GoogleNetHttpTransport.newTrustedTransport();
        final var sheetID = "1f4IlMSLRutavYJeyEx0G4jy2OdnSNLt8iQ8rl6HcBXg";
        final var range = "070000!B5";
        var service = new Sheets.Builder(transport, GoogleConstants.JSON_FACTORY, GoogleConstants.getCredentials(transport))
                .setApplicationName(GoogleConstants.APP_NAME)
                .build();
        var response = service.spreadsheets().values()
                .get(sheetID, range)
                .execute();

        var values = response.getValues();
        Assertions.assertNotNull(values);
        Assertions.assertFalse(values.isEmpty());
        values.forEach(l -> l.forEach(System.out::println));
    }
}
