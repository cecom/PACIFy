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

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import com.geewhiz.pacify.model.PFile;

public class PFileFinder extends SimpleFileVisitor<Path> {

    private final PathMatcher matcher;

    private List<Path>        parents;
    private List<String>      foundFiles;

    PFileFinder(PFile pFile) {
        matcher = FileSystems.getDefault().getPathMatcher("regex:" + pFile.getRelativePath());
        foundFiles = new ArrayList<String>();
        parents = new ArrayList<Path>();
    }

    void evaluate(Path file) {
        Path name = file.getFileName();
        if (name != null && matcher.matches(name)) {
            String finalName = "";
            for (Path parent : parents) {
                if (parent.equals(parents.get(0))) {
                    // first parent is the folder of the marker file, skip this
                    continue;
                }
                if (!parent.equals(parents.get(1)))
                    finalName += "/";
                finalName += parent.getFileName().toString();
            }
            if (parents.size() > 1) {
                finalName += "/";
            }
            finalName += name.getFileName().toString();
            foundFiles.add(finalName);
        }
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
        evaluate(file);
        return CONTINUE;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
        parents.add(dir);
        evaluate(dir);
        return CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
        parents.remove(parents.size() - 1);
        return CONTINUE;
    }

    public List<String> getFiles() {
        return foundFiles;
    }

}
