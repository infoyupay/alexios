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

package org.yupay.alexios.vault;

import org.jetbrains.annotations.Contract;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * This class contains important local static paths.
 * Some useful methods to manage them are implemented as well.
 *
 * @author InfoYupay SACS
 * @version 1.0
 */
public final class LocalPaths {
    /**
     * Contains a path to user.home
     */
    public static final Path USER_HOME = Paths.get(System.getProperty("user.home"));
    /**
     * Contains a path to USER_HOME/.yupay/alexios
     */
    public static final Path ALEXIOS_HOME = USER_HOME.resolve(".yupay", "alexios");
    /**
     * Contains a path to ALEXIOS_HOME/GoogleCredential.json
     */
    public static final Path GOOGLE_CREDENTIAL = ALEXIOS_HOME.resolve("GoogleCredential.json");
    /**
     * Contains a path to ALEXIOS_HOME/gapi_tokens
     */
    public static final Path GOOGLE_TOKENS = ALEXIOS_HOME.resolve("gapi_tokens");

    /**
     * Private constructor that always fail, avoiding instanciation of this class.
     *
     * @throws IllegalAccessException always.
     */
    @Contract("->fail")
    private LocalPaths() throws IllegalAccessException {
        throw new IllegalAccessException("Utility classes shall not be instantiated.");
    }

    /**
     * Convenient method to clean the credential file (writes byte[0]).
     *
     * @throws IOException if unable to write.
     */
    public static void cleanCredential() throws IOException {
        Files.write(GOOGLE_CREDENTIAL, new byte[0]);
    }

    /**
     * Cleans the token folder by deleting each file.
     *
     * @throws IOException if unable to delete.
     */
    public static void cleanTokens() throws IOException {
        try (var children = Files.list(GOOGLE_TOKENS)) {
            for (var it = children.iterator(); it.hasNext(); ) {
                var p = it.next();
                Files.deleteIfExists(p);
            }
        }
    }

    /**
     * Installs a credential file by copying and replacing to {@link #GOOGLE_CREDENTIAL}.
     *
     * @param source the source path with the credential.
     * @throws IOException if unable to copy.
     */
    public static void installCredential(Path source) throws IOException {
        if (!Files.exists(ALEXIOS_HOME)) {
            Files.createDirectories(ALEXIOS_HOME);
        }
        Files.copy(
                source,
                GOOGLE_CREDENTIAL,
                StandardCopyOption.COPY_ATTRIBUTES, StandardCopyOption.REPLACE_EXISTING);
    }
}
