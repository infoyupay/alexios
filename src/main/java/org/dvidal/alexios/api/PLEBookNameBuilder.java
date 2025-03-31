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

package org.dvidal.alexios.api;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * Builder to easily create the PLE file name.
 *
 * @author InfoYupay SACS
 * @version 1.0
 */
public class PLEBookNameBuilder {
    /**
     * The RUC number (Tax ID) of the tax payer.
     */
    private String ruc;
    /**
     * The period year.
     */
    private String year;
    /**
     * The period month.
     */
    private String month;
    /**
     * The period day (only for inventory and balances).
     * For all other books, a default value of "00" is used.
     */
    private String day = "00";
    /**
     * The ID of the Book.
     */
    private String bookID;
    /**
     * The oportunity (only for inventory and balances).
     * For all other books, a default value of "00" is used.
     */
    private String oportunity = "00";
    /**
     * Operations flag ("0": Closure of entity. "1": Operative entity. "2": Book closure).
     */
    private String opsFlag;
    /**
     * Empty flag, if true the book will contain information.
     */
    private boolean infoFlag;
    /**
     * The file extension. Default value is TXT,
     * but some books requires a PDF format, so
     * developer may change this field to achieve that.
     *
     * @implNote the extension musn't include a dot (.), only
     * the extension itself.
     */
    private String extension = "TXT";

    /**
     * Fluent setter - with.
     *
     * @param ruc new value to set in {@link #ruc}
     * @return this instance.
     */
    public final PLEBookNameBuilder withRuc(String ruc) {
        this.ruc = ruc;
        return this;
    }

    /**
     * Fluent setter - with.
     *
     * @param year new value to set in {@link #year}
     * @return this instance.
     */
    public final PLEBookNameBuilder withYear(String year) {
        this.year = year;
        return this;
    }

    /**
     * Fluent setter - with.
     *
     * @param month new value to set in {@link #month}
     * @return this instance.
     */
    public final PLEBookNameBuilder withMonth(String month) {
        this.month = month;
        return this;
    }

    /**
     * Fluent setter - with.
     *
     * @param day new value to set in {@link #day}
     * @return this instance.
     */
    @SuppressWarnings("unused")
    public final PLEBookNameBuilder withDay(String day) {
        this.day = day;
        return this;
    }

    /**
     * Fluent setter - with.
     *
     * @param bookID new value to set in {@link #bookID}
     * @return this instance.
     */
    public final PLEBookNameBuilder withBookID(String bookID) {
        this.bookID = bookID;
        return this;
    }

    /**
     * Fluent setter - with.
     *
     * @param oportunity new value to set in {@link #oportunity}
     * @return this instance.
     */
    @SuppressWarnings("unused")
    public final PLEBookNameBuilder withOportunity(String oportunity) {
        this.oportunity = oportunity;
        return this;
    }

    /**
     * Fluent setter - with.
     *
     * @param opsFlag new value to set in {@link #opsFlag}
     * @return this instance.
     */
    public final PLEBookNameBuilder withOpsFlag(String opsFlag) {
        this.opsFlag = opsFlag;
        return this;
    }

    /**
     * Fluent setter - with.
     *
     * @param infoFlag new value to set in {@link #infoFlag}
     * @return this instance.
     */
    public final PLEBookNameBuilder withEmpty(boolean infoFlag) {
        this.infoFlag = infoFlag;
        return this;
    }

    /**
     * Fluent setter - with.
     *
     * @param extension new value to set in {@link #extension}
     * @return this instance.
     */
    @SuppressWarnings("unused")
    public final PLEBookNameBuilder withExtension(String extension) {
        this.extension = extension;
        return this;
    }


    /**
     * Final operation that concatenates all given parameters as stated
     * by PLE specification. This string must be used as output file name.
     *
     * @return the output file name.
     */
    @Contract(pure = true)
    public final @NotNull String build() {
        return "LE" +
                ruc +
                year +
                month +
                day +
                bookID +
                oportunity +
                opsFlag +
                (infoFlag ? "1" : "0") +
                "1" +
                "1" +
                "." + extension;
    }
}
