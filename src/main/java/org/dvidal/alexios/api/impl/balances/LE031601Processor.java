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

package org.dvidal.alexios.api.impl.balances;

import com.google.api.services.sheets.v4.model.Sheet;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;

import static org.dvidal.alexios.google.GoogleUtils.infoFlag;

record LE031601Processor(Params03 params,
                         File target,
                         Sheet aSheet) implements Callable<File> {
    @Override
    public File call() throws IOException {
        var r = new File(target,
                params.compileFile("031601", infoFlag(aSheet)));
        if (r.exists() && !r.delete())
            throw new IOException("File already exists but cannot be deleted: " + r);
        return r;
    }
}
