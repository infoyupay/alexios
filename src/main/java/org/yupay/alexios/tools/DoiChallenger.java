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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.IntUnaryOperator;

/**
 * This will challenge the doiNumber against the doiType.
 *
 * @param doiType   the doiType value.
 * @param doiNumber the doiNumber value.
 * @author InfoYupay SACS
 * @version 1.0
 */
public record DoiChallenger(@Nullable String doiType, @NotNull String doiNumber, int rowIndex) {
    /**
     * The factor to multiply the RUC digit at each position.
     */
    private static final int[] RUC_FACTORS = {5, 4, 3, 2, 7, 6, 5, 4, 3, 2, 0};

    /**
     * Challenges the doiNumber validity depending upon doiType.
     * The rules for doi number validity are set in SUNAT-PLE specifications
     * and are the following:
     * <table cellspacing="0">
     *     <tr >
     *         <td style="border-width:1px;border-style:solid;border-collapse:collapse;border-spacing:0px">Doi Type</td>
     *         <td style="border-width:1px;border-style:solid;border-collapse:collapse;border-spacing:0px">Description</td>
     *         <td style="border-width:1px;border-style:solid;border-collapse:collapse;border-spacing:0px">Length</td>
     *         <td style="border-width:1px;border-style:solid;border-collapse:collapse;border-spacing:0px">Class</td>
     *         <td style="border-width:1px;border-style:solid;border-collapse:collapse;border-spacing:0px">Length Type</td>
     *     </tr>
     *     <tr >
     *         <td style="border-width:1px;border-style:solid;border-collapse:collapse;border-spacing:0px">0</td>
     *         <td style="border-width:1px;border-style:solid;border-collapse:collapse;border-spacing:0px">OTHERS</td>
     *         <td style="border-width:1px;border-style:solid;border-collapse:collapse;border-spacing:0px">15</td>
     *         <td style="border-width:1px;border-style:solid;border-collapse:collapse;border-spacing:0px">Alfanum</td>
     *         <td style="border-width:1px;border-style:solid;border-collapse:collapse;border-spacing:0px">Up to</td>
     *     </tr>
     *     <tr >
     *         <td style="border-width:1px;border-style:solid;border-collapse:collapse;border-spacing:0px">1</td>
     *         <td style="border-width:1px;border-style:solid;border-collapse:collapse;border-spacing:0px">ID card (DNI)</td>
     *         <td style="border-width:1px;border-style:solid;border-collapse:collapse;border-spacing:0px">8</td>
     *         <td style="border-width:1px;border-style:solid;border-collapse:collapse;border-spacing:0px">Digit only</td>
     *         <td style="border-width:1px;border-style:solid;border-collapse:collapse;border-spacing:0px">Exactly</td>
     *     </tr>
     *     <tr >
     *         <td style="border-width:1px;border-style:solid;border-collapse:collapse;border-spacing:0px">4</td>
     *         <td style="border-width:1px;border-style:solid;border-collapse:collapse;border-spacing:0px">Foreigner ID card</td>
     *         <td style="border-width:1px;border-style:solid;border-collapse:collapse;border-spacing:0px">12</td>
     *         <td style="border-width:1px;border-style:solid;border-collapse:collapse;border-spacing:0px">Alfanum</td>
     *         <td style="border-width:1px;border-style:solid;border-collapse:collapse;border-spacing:0px">Up to</td>
     *     </tr>
     *     <tr >
     *         <td style="border-width:1px;border-style:solid;border-collapse:collapse;border-spacing:0px">6</td>
     *         <td style="border-width:1px;border-style:solid;border-collapse:collapse;border-spacing:0px">RUC (Tax ID)</td>
     *         <td style="border-width:1px;border-style:solid;border-collapse:collapse;border-spacing:0px">11</td>
     *         <td style="border-width:1px;border-style:solid;border-collapse:collapse;border-spacing:0px">Digits</td>
     *         <td style="border-width:1px;border-style:solid;border-collapse:collapse;border-spacing:0px">{@link #challengeRUC(String)}</td>
     *     </tr>
     *     <tr >
     *         <td style="border-width:1px;border-style:solid;border-collapse:collapse;border-spacing:0px">7</td>
     *         <td style="border-width:1px;border-style:solid;border-collapse:collapse;border-spacing:0px">PASSPORT</td>
     *         <td style="border-width:1px;border-style:solid;border-collapse:collapse;border-spacing:0px">12</td>
     *         <td style="border-width:1px;border-style:solid;border-collapse:collapse;border-spacing:0px">Alfanum</td>
     *         <td style="border-width:1px;border-style:solid;border-collapse:collapse;border-spacing:0px">Up to</td>
     *     </tr>
     *     <tr >
     *         <td style="border-width:1px;border-style:solid;border-collapse:collapse;border-spacing:0px">A</td>
     *         <td style="border-width:1px;border-style:solid;border-collapse:collapse;border-spacing:0px">Diplomat ID card</td>
     *         <td style="border-width:1px;border-style:solid;border-collapse:collapse;border-spacing:0px">15</td>
     *         <td style="border-width:1px;border-style:solid;border-collapse:collapse;border-spacing:0px">Digits</td>
     *         <td style="border-width:1px;border-style:solid;border-collapse:collapse;border-spacing:0px">Exactly</td>
     *     </tr>
     * </table>
     *
     * @return true if doiNumber is valid for doiType.
     */
    public boolean challenge() {
        return switch (doiType) {
            case "0" -> doiNumber.matches("\\p{Alnum}{1,15}");
            case "1" -> doiNumber.matches("\\d{8}");
            case "4", "7" -> doiNumber.matches("\\p{Alnum}{1,12}");
            case "6" -> challengeRUC(doiNumber);
            case "A" -> doiNumber.matches("\\d{15}");
            case null, default -> false;
        };
    }

    /**
     * Convenient method to challenge ruc number to ensure it is an 11-digits
     * String and complies with module 11 algorythm. This has been reverse
     * engineered, full discussion at
     * <a href="https://es.stackoverflow.com/questions/42957/c%C3%B3mo-validar-un-ruc-de-per%C3%BA">StackOverflow</a>.
     *
     * @param ruc the ruc number to challenge.
     * @return true if passes validation, false otherwise.
     */
    public boolean challengeRUC(String ruc) {
        //If ruc is null, blank or doesn't contain 11 numeric digits, don't analyze.
        if (ruc == null || ruc.isBlank() || !ruc.matches("\\d{11}")) {
            return false;
        }

        //commpute check digit.
        var cd = 11 - (ruc.chars()
                .map(Character::getNumericValue)
                .map(new IntUnaryOperator() {
                    int i = 0;

                    @Override
                    public int applyAsInt(int operand) {
                        return operand * RUC_FACTORS[i++];
                    }
                })
                .sum() % 11);
        //If check digit is >9, just take the last digit.
        cd %= 10;
        //Compare check digit at RUC and computed check digit.
        return ruc.charAt(10) == Character.forDigit(cd, 10);
    }

    /**
     * Convenient method to negate challenge result.
     *
     * @return true if challenge fail, false otherwise.
     */
    public boolean challengeFailed() {
        return !challenge();
    }
}
