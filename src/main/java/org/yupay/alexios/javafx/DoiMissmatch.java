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

package org.yupay.alexios.javafx;

import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import org.jetbrains.annotations.NotNull;
import org.yupay.alexios.tools.DoiChallenger;

import java.util.Objects;

/**
 * When using the tool DOI number check, this is the view model entity
 * which contains the missmatched checks result.
 *
 * @author InfoYupay SACS
 * @version 1.0
 */
public class DoiMissmatch {
    private final ReadOnlyStringWrapper doiType =
            new ReadOnlyStringWrapper(this, "doiType");
    private final ReadOnlyStringWrapper doiNum =
            new ReadOnlyStringWrapper(this, "doiNum");
    private final ReadOnlyIntegerWrapper row =
            new ReadOnlyIntegerWrapper(this, "row");

    /**
     * Empty, private constructor.
     */
    private DoiMissmatch() {
    }

    /**
     * Mapping constructor to convert a challenge object into a missmatch.
     *
     * @param challenge the challenge object.
     */
    DoiMissmatch(@NotNull DoiChallenger challenge) {
        doiType.set(challenge.doiType());
        doiNum.set(challenge.doiNumber());
        row.set(challenge.rowIndex());
    }

    /**
     * Creates a fancy missmatch to use in tree view root nodes.
     *
     * @param title the title to show. Will be set in the DoiNum column.
     * @return the title.
     */
    public static @NotNull DoiMissmatch title(String title) {
        return title(title, 0);
    }

    /**
     * Creates a fancy missmatch to use in tree view root nodes.
     *
     * @param title    the title to show. Will be set in the DoiNum column.
     * @param rowCount the children row count.
     * @return the title.
     */
    public static @NotNull DoiMissmatch title(String title, int rowCount) {
        var r = new DoiMissmatch();
        r.doiType.set(null);
        r.doiNum.set(title);
        r.row.set(rowCount);
        return r;
    }

    /**
     * FX Accessor - getter.
     *
     * @return value of {@link #doiTypeProperty()}.get();
     */
    public final String getDoiType() {
        return doiType.get();
    }

    /**
     * The type of DOI.
     *
     * @return JavaFX Property.
     */
    @FxProperty
    public final ReadOnlyStringProperty doiTypeProperty() {
        return doiType.getReadOnlyProperty();
    }

    /**
     * FX Accessor - getter.
     *
     * @return value of {@link #doiNumProperty()}.get();
     */
    public final String getDoiNum() {
        return doiNum.get();
    }

    /**
     * The number of DOI.
     *
     * @return JavaFX Property.
     */
    @FxProperty
    public final ReadOnlyStringProperty doiNumProperty() {
        return doiNum.getReadOnlyProperty();
    }

    /**
     * FX Accessor - getter.
     *
     * @return value of {@link #rowProperty()}.get();
     */
    public final int getRow() {
        return row.get();
    }

    /**
     * The row in the book.
     *
     * @return JavaFX Property.
     */
    @FxProperty
    public final ReadOnlyIntegerProperty rowProperty() {
        return row.getReadOnlyProperty();
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof DoiMissmatch that)) return false;

        return Objects.equals(
                getDoiType(), that.getDoiType())
                && Objects.equals(
                getDoiNum(), that.getDoiNum())
                && getRow() == that.getRow();
    }

    @Override
    public int hashCode() {
        return 31 * Objects.hash(
                getDoiType(),
                getDoiNum(),
                getRow());
    }
}
