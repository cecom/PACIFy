/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.geewhiz.pacify.model.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import com.geewhiz.pacify.model.PFile;

public class PFileResolver {

    PFile pFile;

    public PFileResolver(PFile pFile) {
        this.pFile = pFile;
    }

    public List<PFile> resolve() {
        List<PFile> result = new ArrayList<PFile>();

        if (pFile.isUseRegExResolution()) {
            result.addAll(resolveRegExp());
        } else {
            if (pFile.getFile() == null) {
                pFile.setFile(new File(pFile.getPMarker().getFolder(), pFile.getRelativePath()));
            }
            result.add(pFile);
        }

        return result;
    }

    private List<PFile> resolveRegExp() {
        List<PFile> result = new ArrayList<PFile>();

        PFileFinder finder = new PFileFinder(pFile);

        try {
            Files.walkFileTree(pFile.getPMarker().getFolder().toPath(), finder);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        List<String> pFiles = finder.getFiles();
        for (String file : pFiles) {
            result.add(createPFile(file));
        }
        return result;
    }

    private PFile createPFile(String path) {
        PFile aClone = (PFile) pFile.clone();
        pFile.setFile(new File(pFile.getPMarker().getFolder(), path));

        aClone.setUseRegExResolution(false);
        aClone.setRelativePath(path);

        return aClone;
    }

}
