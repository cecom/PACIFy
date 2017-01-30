/*-
 * ========================LICENSE_START=================================
 * com.geewhiz.pacify.impl
 * %%
 * Copyright (C) 2011 - 2017 Sven Oppermann
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */

package com.geewhiz.pacify.model.utils;

import static java.nio.file.FileVisitResult.CONTINUE;

import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import com.geewhiz.pacify.model.PFile;
import com.geewhiz.pacify.utils.FileUtils;

public class PFileFinder extends SimpleFileVisitor<Path> {

    private PFile      pFile;
    private List<Path> foundFiles;

    PFileFinder(PFile pFile) {
        this.pFile = pFile;
        foundFiles = new ArrayList<Path>();
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
        Path relativeFilePath = pFile.getPMarker().getFolder().toPath().relativize(file);

        if (FileUtils.matches(relativeFilePath, pFile.getRelativePath())) {
            foundFiles.add(file);
        }
        return CONTINUE;
    }

    public List<Path> getFiles() {
        return foundFiles;
    }

}
