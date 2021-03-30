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

package org.dvidal.alexios.api.impl.pdt710;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * Auxiliary entity to get a temporal copy of the required information.
 *
 * @version 1.0
 */
class PDTField {

    /**
     * PDT710, Column 0.
     */
    String doiType;
    /**
     * PDT710, Column 1.
     */
    String doiNum = "";
    /**
     * PDT710, Column 2.
     */
    String inputFlag = "";
    /***
     * PDT710, Column 3.
     */
    String lastName1 = "";
    /**
     * PDT710, Column 4.
     */
    String lastName2 = "";
    /**
     * PDT710, Column 5.
     */
    String name = "";
    /**
     * PDT710, Column 6.
     */
    String legalName = "";
    /**
     * PDT710, Column 7.
     */
    BigDecimal amount = BigDecimal.ZERO;

    /**
     * Sums the given amount to the amount of this instance.
     *
     * @param amount the given amount.
     * @return this instance.
     */
    public PDTField sumAmount(BigDecimal amount) {
        this.amount = this.amount.add(amount);
        return this;
    }

    public PDTField reduceAmount(PDTField another) {
        return sumAmount(another.amount);
    }

    /**
     * Converts a DOIType from a pleID to the PDT710 spec code.
     * Then, sets the converted value into this instance.
     *
     * @param pleID the pleID.
     * @return this instance.
     */
    public PDTField parseDoiType(String pleID) {

        doiType = switch (pleID) {
            case "1" -> "01";
            case "4" -> "04";
            case "6" -> "06";
            case "7" -> "07";
            case "A" -> "A";
            default -> "00";
        };
        return this;
    }

    @Override
    public String toString() {
        return new StringJoiner("|", "", "\r\n")
                .add(doiType)
                .add(doiNum)
                .add(inputFlag)
                .add("%.20S".formatted(lastName1))
                .add("%.20S".formatted(lastName2))
                .add("%.20S".formatted(name))
                .add("%.40S".formatted(legalName))
                .add("%.0f".formatted(amount))
                .toString();
    }

    public BigDecimal toAmount() {
        return amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o instanceof PDTField pdtField) {
            return Objects.equals(doiType, pdtField.doiType) && Objects.equals(doiNum, pdtField.doiNum);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(doiType, doiNum);
    }
}
