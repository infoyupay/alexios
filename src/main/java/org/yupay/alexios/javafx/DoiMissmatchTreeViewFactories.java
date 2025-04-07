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
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn.CellDataFeatures;
import javafx.util.Callback;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Static factories for {@link DoiMissmatch} objects in a TreeView.
 *
 * @author InfoYupay SACS
 * @version 1.0
 */
public class DoiMissmatchTreeViewFactories {

    /**
     * Hidding constructor to enforce utility class.
     *
     * @throws IllegalAccessException always.
     */
    @Contract("->fail")
    private DoiMissmatchTreeViewFactories() throws IllegalAccessException {
        throw new IllegalAccessException("DoiMissmatchTreeViewFactories shall not be instantiated.");
    }

    /**
     * Static factory to create a Callback to use as cell value factory
     * in a TreeTableViewColumn for DoiType.
     *
     * @return a new callback instance.
     */
    @Contract(pure = true, value = "->new")
    public static @NotNull Callback<CellDataFeatures<DoiMissmatch, String>, ObservableValue<String>>
    doiTypeTreeViewValueFactory() {
        return p -> Optional.of(p)
                .map(CellDataFeatures::getValue)
                .map(TreeItem::getValue)
                .map(DoiMissmatch::doiTypeProperty)
                .orElseGet(() -> new ReadOnlyStringWrapper("").getReadOnlyProperty());
    }

    /**
     * Static factory to create a Callback to use as cell value factory
     * in a TreeTableViewColumn for DoiType.
     *
     * @return a new callback instance.
     */
    @Contract(pure = true, value = "->new")
    public static @NotNull Callback<CellDataFeatures<DoiMissmatch, String>, ObservableValue<String>>
    doiNumTreeViewValueFactory() {
        return p -> Optional.of(p)
                .map(CellDataFeatures::getValue)
                .map(TreeItem::getValue)
                .map(DoiMissmatch::doiNumProperty)
                .orElseGet(() -> new ReadOnlyStringWrapper("").getReadOnlyProperty());
    }

    /**
     * Static factory to create a Callback to use as cell value factory
     * in a TreeTableViewColumn for DoiType.
     *
     * @return a new callback instance.
     */
    @Contract(pure = true, value = "->new")
    public static @NotNull Callback<CellDataFeatures<DoiMissmatch, Integer>, ObservableValue<Integer>>
    rowTreeViewValueFactory() {
        return p -> Optional.of(p)
                .map(CellDataFeatures::getValue)
                .map(TreeItem::getValue)
                .map(DoiMissmatch::rowProperty)
                .map(ReadOnlyIntegerProperty::asObject)
                .orElseGet(() -> new ReadOnlyIntegerWrapper(0).getReadOnlyProperty().asObject());
    }
}
