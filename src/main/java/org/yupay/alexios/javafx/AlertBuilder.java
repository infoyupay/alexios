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

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

/**
 * Fluent and easy to use builder for alerts.
 *
 * @author InfoYupay SACS
 * @version 1.0
 */
public class AlertBuilder {

    private final Alert.AlertType type;
    private final ButtonType[] buttons;
    private final Map<ButtonType, Runnable> actions
            = new HashMap<>();
    private String title;
    private String text;
    private String header;

    /**
     * Inner constructor, use one of the static factory methods instead.
     *
     * @param type    the type of alert.
     * @param buttons the buttons.
     */
    private AlertBuilder(Alert.AlertType type, ButtonType... buttons) {
        this.type = type;
        this.buttons = buttons;
    }

    /**
     * Creates a builder instance for a {@link javafx.scene.control.Alert.AlertType#CONFIRMATION}
     * dialog with the options {@link ButtonType#OK} and {@link ButtonType#CANCEL}.
     *
     * @return new builder instance.
     */
    @Contract(" -> new")
    public static @NotNull AlertBuilder questionBuilder() {
        return new AlertBuilder(
                Alert.AlertType.CONFIRMATION,
                ButtonType.YES,
                ButtonType.NO);
    }

    /**
     * Creates a builder instance for a {@link javafx.scene.control.Alert.AlertType#ERROR}
     * dialog with the options {@link ButtonType#OK} and {@link ButtonType#CANCEL}.
     *
     * @return new builder instance.
     */
    @Contract(" -> new")
    public static @NotNull AlertBuilder errorBuilder() {
        return new AlertBuilder(
                Alert.AlertType.ERROR,
                ButtonType.OK,
                ButtonType.CANCEL)
                .withTitle("Error")
                .withText("""
                        Ha ocurrido un error mientras se ejecutaba la aplicación,
                        Aceptar para guardar el volcado de pila, Cancelar
                        para continuar.""");
    }

    /**
     * Creates a builder instance for a {@link javafx.scene.control.Alert.AlertType#INFORMATION}
     * dialog with only the option {@link ButtonType#OK}.
     *
     * @return new builder instance.
     */
    @Contract(" -> new")
    public static @NotNull AlertBuilder informationBuilder() {
        return new AlertBuilder(Alert.AlertType.INFORMATION, ButtonType.OK);
    }

    /**
     * Creates a builder instance for a {@link javafx.scene.control.Alert.AlertType#WARNING}
     * dialog with only the option {@link ButtonType#OK}.
     *
     * @return new builder instance.
     */
    @Contract(" -> new")
    public static @NotNull AlertBuilder warningBuilder() {
        return new AlertBuilder(Alert.AlertType.WARNING, ButtonType.OK).withTitle("¡ATENCIÓN!");
    }

    /**
     * Fluent setter - with.
     *
     * @param title new value to set in field.
     * @return this instance.
     * @see #getTitle()
     */
    @Contract("_->this")
    public final @NotNull AlertBuilder withTitle(String title) {
        this.title = title;
        return this;
    }

    /**
     * The title to show in the alert.
     *
     * @return value of the field.
     */
    public final String getTitle() {
        return title;
    }

    /**
     * Fluent setter - with.
     *
     * @param text new value to set in field.
     * @return this instance.
     * @see #getText() ()
     */
    @Contract("_->this")
    public final @NotNull AlertBuilder withText(String text) {
        this.text = text;
        return this;
    }

    /**
     * The text to show in the alert content text.
     *
     * @return value of the field.
     */
    public final String getText() {
        return text;
    }

    /**
     * Fluent setter - with.
     *
     * @param header new value to set in field.
     * @return this instance.
     */
    @Contract("_->this")
    public final @NotNull AlertBuilder withHeader(String header) {
        this.header = header;
        return this;
    }

    /**
     * The header text to show in the dialog.
     *
     * @return value of the field.
     */
    public final String getHeader() {
        return header;
    }

    /**
     * Registers an action to follow for a given user response.
     *
     * @param button the button constant (user response) for the action.
     * @param action the action to perform on such response.
     * @return this instance.
     */
    @Contract("_,_->this")
    public final @NotNull AlertBuilder registerAction(
            @NotNull ButtonType button,
            @NotNull Runnable action) {
        actions.put(button, action);
        return this;
    }

    /**
     * Builds the alert and invokes show and wait, after waiting
     * for user answer will run the registered action, or none if
     * no action is registered.
     */
    public final void buildShowAndWait() {
        build()
                .showAndWait()
                .map(actions::get)
                .ifPresent(Runnable::run);
    }

    /**
     * Builds the alert and invokes show, won't expect any user
     * response at all. Useful to show information without
     * expecting user to do something.
     */
    public final void buildAndShow() {
        build().show();
    }

    /**
     * Convenient method to handle errors, it'll show a standard message
     * and a header (brief) description of error. If user clicks OK, will
     * be able to save the stacktrace, otherwise the app will continue
     * running.
     *
     * @param header the header (brief) text for user.
     * @param e      the throwable to manage.
     */
    public final void handleFailure(String header, Throwable e) {
        withHeader(header)
                .registerAction(ButtonType.OK, ()
                        -> FileSystemInteractions.chooseFileToSave(
                        new FileChooser.ExtensionFilter("Archivos de texto (.txt)", "*.txt")
                ).ifPresent(p -> {
                    try (var os = Files.newOutputStream(p);
                         var pw = new PrintWriter(os)) {
                        e.printStackTrace(pw);
                    } catch (IOException ex) {
                        throw new RuntimeException("Unable to export error log to txt.", ex);
                    }
                })).buildShowAndWait();
    }

    /**
     * Builds the alert for further usage.
     *
     * @return the built alert.
     */
    public Alert build() {
        Alert alert = new Alert(type, getTitle(), buttons);
        alert.setContentText(getText());
        alert.setHeaderText(getHeader());
        return alert;
    }
}
