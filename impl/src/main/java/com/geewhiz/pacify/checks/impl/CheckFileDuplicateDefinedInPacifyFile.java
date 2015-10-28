package com.geewhiz.pacify.checks.impl;

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

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import com.geewhiz.pacify.checks.PMarkerCheck;
import com.geewhiz.pacify.defect.Defect;
import com.geewhiz.pacify.defect.FileDuplicateDefinedInPMarkerDefect;
import com.geewhiz.pacify.model.PArchive;
import com.geewhiz.pacify.model.PFile;
import com.geewhiz.pacify.model.PMarker;

public class CheckFileDuplicateDefinedInPacifyFile implements PMarkerCheck {

    public LinkedHashSet<Defect> checkForErrors(PMarker pMarker) {
        LinkedHashSet<Defect> defects = new LinkedHashSet<Defect>();

        for (PArchive pArchive : pMarker.getPArchives()) {
            checkPFiles(defects, pMarker, pArchive, pArchive.getPFiles());
        }

        checkPFiles(defects, pMarker, pMarker.getPFiles());
        return defects;
    }

    private void checkPFiles(LinkedHashSet<Defect> defects, PMarker pMarker, List<PFile> pFiles) {
        checkPFiles(defects, pMarker, null, pFiles);
    }

    private void checkPFiles(LinkedHashSet<Defect> defects, PMarker pMarker, PArchive pArchive, List<PFile> pFiles) {
        List<String> files = new ArrayList<String>();
        for (PFile pFile : pFiles) {
            if (files.contains(pFile.getRelativePath())) {
                Defect defect = new FileDuplicateDefinedInPMarkerDefect(pMarker, pArchive, pFile);
                defects.add(defect);
                continue;
            }
            files.add(pFile.getRelativePath());
        }

    }
}
