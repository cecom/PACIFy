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

import java.io.File;
import java.util.LinkedHashSet;

import com.geewhiz.pacify.checks.PMarkerCheck;
import com.geewhiz.pacify.defect.Defect;
import com.geewhiz.pacify.defect.FileDoesNotExistDefect;
import com.geewhiz.pacify.managers.EntityManager;
import com.geewhiz.pacify.model.PArchive;
import com.geewhiz.pacify.model.PFile;
import com.geewhiz.pacify.model.PMarker;
import com.geewhiz.pacify.utils.FileUtils;

public class CheckTargetFileExist implements PMarkerCheck {

    public LinkedHashSet<Defect> checkForErrors(EntityManager entityManager, PMarker pMarker) {
        LinkedHashSet<Defect> defects = new LinkedHashSet<Defect>();

        checkArchiveEntries(entityManager, defects, pMarker);
        checkPFileEntries(entityManager, defects, pMarker);
        return defects;
    }

    private void checkArchiveEntries(EntityManager entityManager, LinkedHashSet<Defect> defects, PMarker pMarker) {
        // TODO: sollte ausgelagert werden in PFile.getFile()...
        for (PArchive pArchive : entityManager.getPArchivesFrom(pMarker)) {
            for (PFile pFile : pArchive.getPFiles()) {
                if (!FileUtils.archiveContainsFile(pMarker, pArchive, pFile)) {
                    defects.add(new FileDoesNotExistDefect(pFile));
                }
            }
        }

    }

    private void checkPFileEntries(EntityManager entityManager, LinkedHashSet<Defect> defects, PMarker pMarker) {
        for (PFile pFile : entityManager.getPFilesFrom(pMarker)) {
            File file = pFile.getFile();
            if (file.exists() && file.isFile()) {
                continue;
            }
            Defect defect = new FileDoesNotExistDefect(pFile);
            defects.add(defect);
        }
    }

}