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

import com.google.api.services.drive.model.File;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * JavaFx bean for Google Drive File.
 *
 * @author InfoYupay SACS
 * @version 1.0
 */
public class GDriveFile {

    private final ReadOnlyStringWrapper fileId =
            new ReadOnlyStringWrapper(this, "fileId");
    private final ReadOnlyStringWrapper fileName =
            new ReadOnlyStringWrapper(this, "fileName");
    private final ReadOnlyStringWrapper type =
            new ReadOnlyStringWrapper(this, "type");

    /**
     * Empty consstructor to allow the creation of empty value.
     */
    private GDriveFile() {
    }

    /**
     * Constructor that extracts inner fields values from a google drive file.
     *
     * @param file the google drive file object.
     */
    public GDriveFile(@NotNull File file) {
        fileId.setValue(file.getId());
        fileName.setValue(file.getName());
        type.set(file.getMimeType());
    }

    /**
     * Empty instance for some UI components.
     *
     * @return an instance with no ID and toString "Seleccionar..."
     */
    @Contract(" -> new")
    public static @NotNull GDriveFile empty() {
        return new GDriveFile() {
            @Override
            public String toString() {
                return "(Seleccionar...)";
            }
        };
    }

    /**
     * FX Accessor - getter.
     *
     * @return value of {@link #fileIdProperty()}.get();
     */
    public final String getFileId() {
        return fileId.get();
    }

    /**
     * Property for FileId from Google Drive API.
     *
     * @return JavaFX Property.
     */
    @FxProperty
    public final ReadOnlyStringProperty fileIdProperty() {
        return fileId.getReadOnlyProperty();
    }

    /**
     * FX Accessor - getter.
     *
     * @return value of {@link #fileNameProperty()}.get();
     */
    public final String getFileName() {
        return fileName.get();
    }

    /**
     * Property for FileName from Google Drive API.
     *
     * @return JavaFX Property.
     */
    @FxProperty
    public final ReadOnlyStringProperty fileNameProperty() {
        return fileName.getReadOnlyProperty();
    }

    /**
     * FX Accessor - getter.
     *
     * @return value of {@link #typeProperty()}.get();
     */
    public final String getType() {
        return type.get();
    }

    /**
     * MIME type of google drive file.
     *
     * @return JavaFX Property.
     */
    @FxProperty
    public final ReadOnlyStringProperty typeProperty() {
        return type.getReadOnlyProperty();
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof GDriveFile that)) return false;

        return Objects.equals(getFileId(), that.getFileId())
                && Objects.equals(getFileName(), that.getFileName())
                && Objects.equals(getType(), that.getType());
    }

    @Override
    public int hashCode() {
        return 31 * Objects.hash(getFileId(), getFileName(), getType());
    }

    @Override
    public String toString() {
        return getFileName();
    }
}
