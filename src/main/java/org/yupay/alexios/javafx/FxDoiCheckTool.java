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

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableView;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.yupay.alexios.tools.DoiChallenger;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Map;

/**
 * Creates a Diloag to show the DOI check results.
 * Since this dialog purpose isn't ask user something,
 * any user input will be converted into a null result.
 *
 * @author InfoYupay SACS
 * @version 1.0
 */
public class FxDoiCheckTool extends Dialog<Object> {

    @FXML
    private DialogPane top;
    @FXML
    private TreeTableView<DoiMissmatch> ttbResults;

    /**
     * Creates an empty controller instance.
     */
    public FxDoiCheckTool() {
    }

    /**
     * Static factory to create from FXML loader.
     *
     * @return the created controller.
     */
    @Contract("->new")
    public static FxDoiCheckTool create() {
        try {
            var loader = new FXMLLoader(FxDoiCheckTool.class.getResource("doi_check_tool.fxml"));
            loader.load();
            return loader.getController();
        } catch (IOException e) {
            throw new UncheckedIOException("FXML file: doi_check_tool.fxml", e);
        }
    }

    /**
     * FXML initializer.
     */
    @FXML
    void initialize() {
        setDialogPane(top);
        setTitle("Verificación de Documentos de Identidad");
        setResultConverter(_ -> null);
    }

    /**
     * Convenient method to accept a map of challenges and convert them into
     * a root tree item. Only mismatched DOI numbers will be filtered.
     *
     * @param challenges the map of worksheets - challenges objects.
     */
    void checkDoiFromMap(@NotNull Map<String, List<DoiChallenger>> challenges) {
        var ls = challenges.entrySet()
                .stream()
                .map(this::filterAndCollectMapEntry)
                .toList();
        var r = new TreeItem<>(DoiMissmatch.title("-"));
        r.getChildren().setAll(ls);
        ttbResults.setRoot(r);
    }

    /**
     * Convenient method to convert an entry of String - Challenger into a tree item of
     * DOI missmatches.
     *
     * @param entry the entry item.
     * @return a tree item containing only mismatched doi numbers.
     */
    private @NotNull TreeItem<DoiMissmatch> filterAndCollectMapEntry(Map.@NotNull Entry<String, List<DoiChallenger>> entry) {
        var ls = filterAndWrap(entry.getValue());
        var r = new TreeItem<>(DoiMissmatch.title(entry.getKey(), ls.size()));
        r.getChildren().setAll(ls);
        return r;
    }

    /**
     * Converts a list of challengers into a list of tree items containing only missmatched DOI numbers.
     *
     * @param challengers the challengers list.
     * @return a tree item list.
     */
    @Contract("_->new")
    private List<TreeItem<DoiMissmatch>> filterAndWrap(@NotNull List<DoiChallenger> challengers) {
        return challengers
                .stream()
                .filter(DoiChallenger::challengeFailed)
                .map(this::createItem)
                .toList();
    }

    /**
     * Converts a single DoiChallenger item into a DoiMissmatch object wrapped in a TreeItem.
     *
     * @param item the item to convert.
     * @return a tree item object.
     */
    @Contract("_ -> new")
    private @NotNull TreeItem<DoiMissmatch> createItem(DoiChallenger item) {
        return new TreeItem<>(new DoiMissmatch(item));
    }
}
