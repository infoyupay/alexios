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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.FieldSource;

import java.util.List;

import static org.junit.jupiter.params.provider.Arguments.arguments;

/**
 * Tests the doi challenges.
 *
 * @author InfoYupay SACS
 * @version 1.0
 */
class DoiChallengerTest {
    @SuppressWarnings("unused")
    static List<Arguments> doiChallenges = List.of(
            arguments("", "", false),
            arguments(null, "", false),
            arguments("0", "12123.3k.f-;-", false),
            arguments("0", "12123FKSL", true),
            arguments("0", "1234567890123456", false),
            arguments("1", "12123FKS", false),
            arguments("1", "12345678", true),
            arguments("4", "012345678", true),
            arguments("4", "A12345678", true),
            arguments("4", "123456789ABC", true),
            arguments("4", "123456789ABC1", false),
            arguments("4", "123456789A.;", false),
            arguments("6", "12123FKSL", false),
            arguments("6", "", false),
            arguments("6", "20604427909", true),
            arguments("6", "20101161634", true),
            arguments("6", "20100027021", true),
            arguments("6", "10700968133", true),
            arguments("6", "10704425720", true),
            arguments("6", "10704425721", false),
            arguments("7", "012345678", true),
            arguments("7", "A12345678", true),
            arguments("7", "123456789ABC", true),
            arguments("7", "123456789ABC1", false),
            arguments("7", "123456789A.;", false),
            arguments("A", "12123FKSL", false),
            arguments("A", "1234", false),
            arguments("A", "1234567890123456", false),
            arguments("A", "12345678901234A", false),
            arguments("A", "123456789012345", true)
    );

    @ParameterizedTest
    @FieldSource("doiChallenges")
    void testDoiChallenger(String doiType, String doiNumber, boolean expected) {
        if (expected) {
            Assertions.assertTrue(new DoiChallenger(doiType, doiNumber, 0).challenge());
        } else {
            Assertions.assertFalse(new DoiChallenger(doiType, doiNumber, 0).challenge());
        }
    }
}
