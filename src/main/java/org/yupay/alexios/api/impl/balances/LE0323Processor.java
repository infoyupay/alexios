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

package org.yupay.alexios.api.impl.balances;

import com.google.api.services.sheets.v4.model.Sheet;
import org.yupay.alexios.google.GoogleUtils;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.concurrent.Callable;

import static org.yupay.alexios.google.GoogleUtils.infoFlag;

/**
 * Processor (callable) to download a file from google drive and store it with the
 * compiled file name as required by SUNAT-PLE specs 03230 (financial statement notes).
 *
 * @param params the parameters to compile the filename.
 * @param target the target output path.
 * @param aSheet worksheet containing the drive id at 2,2 cell.
 * @author InfoYupay SACS
 * @version 1.0
 */
record LE0323Processor(Params03 params,
                       Path target,
                       Sheet aSheet) implements Callable<Path> {

    @Override
    public @NotNull Path call() throws Exception {
        //Check information flag.
        var iflg = infoFlag(aSheet);
        //Compile the file name.
        var r = target.resolve(params.compileFile(
                "032300",
                iflg,
                "pdf"));
        //Create an empty file.
        GoogleUtils.recreateFile(r);
        //Of information flag is true...
        if (iflg) {
            //Get the google drive file token/id.
            var driveToken = GoogleUtils.stringAt(aSheet.getData().getFirst(), 2, 2);
            //Download file to destination.
            GoogleUtils.downloadDriveFile(driveToken, r);
        }
        return r;
    }
}
