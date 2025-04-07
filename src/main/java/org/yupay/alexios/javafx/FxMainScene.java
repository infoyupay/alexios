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

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.yupay.alexios.api.BookProcessor;
import org.yupay.alexios.api.impl.assets.AssetsProcessor;
import org.yupay.alexios.api.impl.balances.BalanceProcessor;
import org.yupay.alexios.api.impl.costs.CostsProcessor;
import org.yupay.alexios.api.impl.pdt710.PDTProcessor;
import org.yupay.alexios.google.GoogleUtils;
import org.yupay.alexios.tools.DoiNumberCheck;
import org.yupay.alexios.vault.LocalPaths;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.security.GeneralSecurityException;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * JavaFx controller for the Main scene GUI.
 *
 * @author InfoYupay SACS
 * @version 1.0
 */
public class FxMainScene {

    /*+++++++++++++++++++++++++++*
     * FX Property declarations. *
     *+++++++++++++++++++++++++++*/
    private final ReadOnlyObjectWrapper<GDriveFile> book0300File =
            new ReadOnlyObjectWrapper<>(this, "book0300File", GDriveFile.empty());
    private final ReadOnlyObjectWrapper<GDriveFile> book0700File =
            new ReadOnlyObjectWrapper<>(this, "book0700File", GDriveFile.empty());
    private final ReadOnlyObjectWrapper<GDriveFile> book1000File =
            new ReadOnlyObjectWrapper<>(this, "book1000File", GDriveFile.empty());
    /*++++++++++++++++++*
     * FXML components. *
     *++++++++++++++++++*/
    @FXML
    private Scene top;
    @FXML
    private WebView webAbout;
    @SuppressWarnings("unused")
    @FXML
    private TitledPane pnlAbout;

    /**
     * Creates an empty scene controller.
     */
    public FxMainScene() {
    }

    /**
     * Static factory to load this controller from main_scene.fxml source.
     *
     * @return a new main scene.
     */
    @Contract("->new")
    public static FxMainScene create() {
        try {
            var loader = new FXMLLoader(FxMainScene.class.getResource("main_scene.fxml"));
            loader.load();
            return loader.getController();
        } catch (IOException e) {
            throw new UncheckedIOException("Error within main_scene.fxml", e);
        }
    }

    /**
     * FX Accessor - getter.
     *
     * @return value of {@link #book0300FileProperty()}.get();
     */
    public final GDriveFile getBook0300File() {
        return book0300File.get();
    }

    /**
     * File metadata for book LE0300 - Inventory and statements.
     *
     * @return JavaFX Property.
     */
    @FxProperty
    public final ReadOnlyObjectProperty<GDriveFile> book0300FileProperty() {
        return book0300File.getReadOnlyProperty();
    }

    /**
     * FX Accessor - getter.
     *
     * @return value of {@link #book0700FileProperty()}.get();
     */
    public final GDriveFile getBook0700File() {
        return book0700File.get();
    }

    /**
     * Google drive file information property for book 0700 - Assets.
     *
     * @return JavaFX Property.
     */
    @FxProperty
    public final ReadOnlyObjectProperty<GDriveFile> book0700FileProperty() {
        return book0700File.getReadOnlyProperty();
    }

    /**
     * FX Accessor - getter.
     *
     * @return value of {@link #book1000FileProperty()}.get();
     */
    public final GDriveFile getBook1000File() {
        return book1000File.get();
    }

    /**
     * The google drive file information for book 10000 - Costs.
     *
     * @return JavaFX Property.
     */
    @FxProperty
    public final ReadOnlyObjectProperty<GDriveFile> book1000FileProperty() {
        return book1000File.getReadOnlyProperty();
    }

    /**
     * Loads about html file.
     */
    private void loadAbout() {
        try (var inp = getClass().getResourceAsStream("about.html");
             var isr = new InputStreamReader(Objects.requireNonNull(inp));
             var br = new BufferedReader(isr)) {
            var str = br.lines().collect(Collectors.joining("\n"));
            webAbout.getEngine().loadContent(str);
        } catch (IOException e) {
            throw new UncheckedIOException("about.html unable to load", e);
        }
    }

    /**
     * Adds the top scene of this controller to a stage.
     *
     * @param stage the stage.
     */
    public void addToStage(@NotNull Stage stage) {
        stage.setScene(top);
        loadAbout();
    }

    /**
     * FXML event handler to install credential action.
     */
    @FXML
    void installCredentialAction() {
        FileSystemInteractions.chooseFileToOpen(
                        new FileChooser.ExtensionFilter("Archivos json", "*.json"),
                        new FileChooser.ExtensionFilter("Archivos txt", "*.txt"),
                        new FileChooser.ExtensionFilter("Todos los archivos", "*.*"))
                .ifPresent(p -> {
                    try {
                        LocalPaths.installCredential(p);
                        AlertBuilder.informationBuilder()
                                .withTitle("Operación Completada")
                                .withText("Se logró instalar correctamente la credencial en esta computadora.")
                                .buildAndShow();
                    } catch (IOException e) {
                        AlertBuilder.errorBuilder()
                                .handleFailure("No se pudo copiar la credencial de google localmente.", e);
                    }
                });
    }

    /**
     * FXML event handler for clean credential action.
     */
    @FXML
    void cleanCredentialAction() {
        AlertBuilder.questionBuilder()
                .withTitle("CONFIRMACIÓN REQUERIDA")
                .withHeader("¿Estás seguro? ¡NO PUEDO DESHACER ESTA OPERACIÓN!")
                .withText("""
                        Estás a punto de destruir tu credencial de Google API almacenada
                        en esta computadora. Esta acción no puede deshacerse.""")
                .registerAction(ButtonType.YES, () -> {
                    try {
                        LocalPaths.cleanCredential();
                        AlertBuilder.informationBuilder()
                                .withTitle("Operación Completada")
                                .withText("Se logró borrar tu credencial localmente.")
                                .buildAndShow();
                    } catch (IOException e) {
                        AlertBuilder.errorBuilder()
                                .handleFailure("No se pudo eliminar la credencial.", e);
                    }
                });
    }

    /**
     * FXML event handler for clean tokens action.
     */
    @FXML
    void cleanTokensAction() {
        AlertBuilder.questionBuilder()
                .withTitle("CONFIRMACIÓN REQUERIDA")
                .withHeader("¿Estás seguro? ¡NO PUEDO DESHACER ESTA OPERACIÓN!")
                .withText("""
                        Estás a punto de destruir tus tokens de Google API almacenadas
                        en esta computadora. Esta acción no puede deshacerse.""")
                .registerAction(ButtonType.YES, () -> {
                    try {
                        LocalPaths.cleanTokens();
                        AlertBuilder.informationBuilder()
                                .withTitle("Operación Completada")
                                .withText("Se lograron borrar tus tokens GApi localmente.")
                                .buildAndShow();
                    } catch (IOException e) {
                        AlertBuilder.errorBuilder()
                                .handleFailure("No se pudieron eliminar los tokens.", e);
                    }
                });
    }

    /**
     * FXML event handler for the export action.
     *
     * @param event the event object.
     */
    @FXML
    void exportAction(@NotNull ActionEvent event) {
        //Only proceed if the event source is a button.
        if (!(event.getSource() instanceof Button btn)) {
            return;
        }
        //Variables to initialize within a switch case of button user data.
        GDriveFile input;
        BookProcessor processor;
        switch (Objects.toString(btn.getUserData())) {
            case "0300" -> {
                input = book0300File.get();
                processor = new BalanceProcessor();
            }
            case "710" -> {
                input = book0300File.get();
                processor = new PDTProcessor();
            }
            case "0700" -> {
                input = book0700File.get();
                processor = new AssetsProcessor();
            }
            case "1000" -> {
                input = book1000File.get();
                processor = new CostsProcessor();
            }
            default -> {
                input = null;
                processor = null;
            }
        }
        //If input is null, user must select source file alert.
        if (input == null) {
            AlertBuilder.warningBuilder()
                    .withText("Primero tienes que seleccionar el archivo de origen.")
                    .buildAndShow();
            return;
        }
        //Choose a directory for exportation.
        FileSystemInteractions
                .chooseDirectory()
                //if user has choosen an output folder.
                .ifPresent(p -> {
                    try {
                        //retrieve  spreadsheet.
                        var sheet = GoogleUtils.getSpreadsheet(input.getFileId());
                        //procees spreadsheet.
                        processor.processSheet(sheet, p);
                        //Show confirmation that exportation has been completed.
                        AlertBuilder.informationBuilder()
                                .withText("Se ha completado la generación del libro %s exitosamente."
                                        .formatted(btn.getUserData()))
                                .withTitle("Opración Completada")
                                .buildAndShow();
                    } catch (Exception e) {
                        AlertBuilder.errorBuilder()
                                .handleFailure(
                                        "Ocurrió un error al generar el libro %s"
                                                .formatted(btn.getUserData()),
                                        e);
                    }
                });
    }

    /**
     * FXML event handler for set link action. This action is triggered
     * when user right clicks the hyperlink component, then pastes/types
     * the input source link, and presses enter.
     *
     * @param event the event object.
     */
    @FXML
    void setLinkAction(@NotNull ActionEvent event) {
        if (event.getSource() instanceof TextField txt) {
            var str = Objects.requireNonNullElse(txt.getText(), "").strip();
            txt.clear();
            loadGoogleMetadata(str, Objects.toString(txt.getUserData()));
        }
    }

    /**
     * FXML event handler for input drag entered.
     * It'll mark with a red border the drag entered hyperlink.
     *
     * @param event the event object.
     */
    @FXML
    void inputDragEntered(@NotNull DragEvent event) {
        if (event.getSource() instanceof Hyperlink lnk) {
            if (event.getDragboard().hasString()
                    || event.getDragboard().hasUrl()) {
                lnk.setBorder(new Border(
                        new BorderStroke(Color.CRIMSON,
                                BorderStrokeStyle.SOLID,
                                CornerRadii.EMPTY,
                                BorderWidths.DEFAULT)));
            }
        }
        event.consume();
    }

    /**
     * FXML event handler for input drag exited.
     * It'll reset the border on the drag exited hyperlink.
     *
     * @param event the event object.
     */
    @FXML
    void inputDragExited(@NotNull DragEvent event) {
        if (event.getSource() instanceof Hyperlink lnk) {
            lnk.setBorder(Border.EMPTY);
        }
        event.consume();
    }

    /**
     * FXML event handler for input drag over.
     * It'll accept link and copy mode for strings or url.
     *
     * @param event the event object.
     */
    @FXML
    void inputDragOver(@NotNull DragEvent event) {
        if (event.getDragboard().hasUrl()
                || event.getDragboard().hasString()) {
            event.acceptTransferModes(TransferMode.LINK, TransferMode.COPY);
        }
        event.consume();
    }

    /**
     * FXML event handler for input drag drop.
     * It'll check if the URL or text link contains a valid address
     * to the google suite worksheet object.
     *
     * @param event the event object.
     */
    @FXML
    void inputDragDrop(@NotNull DragEvent event) {
        var db = event.getDragboard();
        //check that gesture target is an hyperlink control.
        if (event.getGestureTarget() instanceof Hyperlink lnk) {
            String value = "";
            //Extract value from dragboard: url or string.
            if (db.hasUrl()) value = Objects.toString(db.getUrl(), "");
            else if (db.hasString()) value = Objects.toString(db.getString(), "");
            //If value doesn't start with http:// or https://, the prefix shall be added.
            if (!value.isBlank() && !value.matches("(?i)https?://.*")) value = "https://" + value;
            //Complete drop and consume event.
            event.setDropCompleted(true);
            event.consume();
            //Load google metadata.
            loadGoogleMetadata(value, Objects.toString(lnk.getUserData()));
        }//if gesture target is not an hyperlink, reject the drop evevnt.
        else {
            event.setDropCompleted(false);
            event.consume();
        }
    }

    /**
     * Convenient method to load google metadata from a given URL and to a given book.
     *
     * @param url  the url string.
     * @param book the book content. Use book ID like in button user data.
     */
    private void loadGoogleMetadata(@NotNull String url, String book) {
        //Validate link text.
        if (!url.isEmpty()) {
            var parts = url.split("/");
            if (parts.length < 6) {
                AlertBuilder.warningBuilder()
                        .withText("El link ingresado es incorrecto.")
                        .buildAndShow();
            } else {
                try {
                    //get metadata as google drive file object.
                    var val = new GDriveFile(GoogleUtils.getMetadata(parts[5]));
                    //check type is google spreadsheet.
                    if (!val.getType().equalsIgnoreCase(GoogleUtils.SPREADSHEET_MIME)) {
                        //If not, show warning.
                        AlertBuilder.warningBuilder()
                                .withText("El archivo no es un libro de spreadsheets.")
                                .buildAndShow();
                    } else {
                        //otherwise, set the value to propper book property.
                        switch (Objects.toString(book)) {
                            case "0300" -> book0300File.set(val);
                            case "0700" -> book0700File.set(val);
                            case "1000" -> book1000File.set(val);
                        }
                    }
                } catch (IOException | GeneralSecurityException e) {
                    AlertBuilder.errorBuilder()
                            .handleFailure("No pudimos obtener los metadatos desde google drive.", e);
                }
            }
        }
    }

    /**
     * FXML event handler for the check DOI number action.
     */
    @FXML
    void checkDoi() {
        //get book file.
        var input = getBook0300File();
        //If no book file has been selected.
        if (input == null) {
            //warn user.
            AlertBuilder.warningBuilder()
                    .withText("Primero tienes que seleccionar el archivo de google drive.")
                    .buildAndShow();
        } else {
            try {
                //get spreadsheet object.
                var sheet = GoogleUtils.getSpreadsheet(input.getFileId());
                //build a map of doi numbers.
                var map = new DoiNumberCheck().processLE03(sheet);
                //create the dialog to show results.
                var dlg = FxDoiCheckTool.create();
                //process map of doi numbers checkers.
                dlg.checkDoiFromMap(map);
                //show dialog.
                dlg.showAndWait();
            } catch (GeneralSecurityException | IOException e) {
                AlertBuilder.errorBuilder()
                        .handleFailure(
                                "No se pudo completar la verificación de documentos de identificación.",
                                e);
            }
        }
    }

}
