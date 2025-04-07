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

import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;

/**
 * Utility class to interact with the file system, like creation of file choosers.
 *
 * @author InfoYupay SACS
 * @version 1.0
 */
public class FileSystemInteractions {

    /**
     * Inner constructor that always fail to avoid instanciating this class.
     *
     * @throws IllegalAccessException always.
     */
    @Contract("->fail")
    private FileSystemInteractions() throws IllegalAccessException {
        throw new IllegalAccessException("FileSystemInteractions should not be instantiated.");
    }

    /**
     * Shows an open file dialog with the given file extension filters.
     *
     * @param filters the file extension filters.
     * @return the choosen file.
     * @see javafx.stage.FileChooser.ExtensionFilter#ExtensionFilter(String, String...)
     */
    public static Optional<Path> chooseFileToOpen(FileChooser.ExtensionFilter... filters) {
        return Optional.ofNullable(
                        buildFileChooser("Abrir...", filters)
                                .showOpenDialog(null))
                .map(File::toPath);
    }

    /**
     * Shows a save file dialog with the given file extension filters.
     *
     * @param filters the file extension filters.
     * @return the choosen file.
     * @see javafx.stage.FileChooser.ExtensionFilter#ExtensionFilter(String, String...)
     */
    public static Optional<Path> chooseFileToSave(FileChooser.ExtensionFilter... filters) {

        return Optional.ofNullable(buildFileChooser("Guardar...", filters)
                        .showSaveDialog(null))
                .map(File::toPath);
    }

    /**
     * Shows a exports to directory dialog.
     *
     * @return the choosen file.
     */
    public static Optional<Path> chooseDirectory() {
        var dirChooser = new DirectoryChooser();
        dirChooser.setTitle("Exportar en...");
        return Optional.ofNullable(dirChooser
                        .showDialog(null))
                .map(File::toPath);
    }

    /**
     * Builds a file dialog with the given file extension filters.
     *
     * @param title   the title of file chooser.
     * @param filters the file extension filters.
     * @return the choosen file.
     * @see javafx.stage.FileChooser.ExtensionFilter#ExtensionFilter(String, String...)
     */
    public static @NotNull FileChooser buildFileChooser(String title, FileChooser.ExtensionFilter... filters) {
        var fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        fileChooser.getExtensionFilters().setAll(filters);
        return fileChooser;
    }

}
