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
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.geewhiz.pacify.checks.PMarkerCheck;
import com.geewhiz.pacify.defect.Defect;
import com.geewhiz.pacify.defect.DefectException;
import com.geewhiz.pacify.defect.NoPlaceholderInTargetFileDefect;
import com.geewhiz.pacify.model.PArchive;
import com.geewhiz.pacify.model.PFile;
import com.geewhiz.pacify.model.PMarker;
import com.geewhiz.pacify.model.PProperty;
import com.geewhiz.pacify.utils.FileUtils;

public class CheckPlaceholderExistsInTargetFile implements PMarkerCheck {

    public List<Defect> checkForErrors(PMarker pMarker) {
        List<Defect> defects = new ArrayList<Defect>();

        checkArchives(pMarker, defects);
        checkPFiles(pMarker, defects);

        return defects;
    }

    private void checkArchives(PMarker pMarker, List<Defect> defects) {
        for (PArchive pArchive : pMarker.getPArchives()) {
            for (PFile pFile : pArchive.getPFiles()) {
                File file;
                try {
                    file = FileUtils.extractFile(pMarker, pArchive, pFile);
                } catch (DefectException e) {
                    // this is checked from another checker, so we dont throw it.
                    continue;
                }
                for (PProperty pProperty : pFile.getPProperties()) {
                    String beginToken = pMarker.getBeginTokenFor(pArchive, pFile);
                    String endToken = pMarker.getEndTokenFor(pArchive, pFile);

                    boolean exists = doesPropertyExistInFile(file, pProperty, pFile.getEncoding(), beginToken, endToken);
                    if (exists) {
                        continue;
                    }
                    Defect defect = new NoPlaceholderInTargetFileDefect(pMarker, pArchive, pFile, pProperty);
                    defects.add(defect);
                }
                file.delete();
            }

        }
    }

    private void checkPFiles(PMarker pMarker, List<Defect> defects) {
        for (PFile pFile : pMarker.getPFiles()) {
            File file = pMarker.getAbsoluteFileFor(pFile);

            if (!file.exists()) {
                // is checked before, so don'‚t throw any exception.
                continue;
            }

            for (PProperty pProperty : pFile.getPProperties()) {
                String beginToken = pMarker.getBeginTokenFor(pFile);
                String endToken = pMarker.getEndTokenFor(pFile);

                boolean exists = doesPropertyExistInFile(file, pProperty, pFile.getEncoding(), beginToken, endToken);
                if (exists) {
                    continue;
                }
                Defect defect = new NoPlaceholderInTargetFileDefect(pMarker, pFile, pProperty);
                defects.add(defect);
            }
        }
    }

    private boolean doesPropertyExistInFile(File file, PProperty pProperty, String encoding, String beginToken, String endToken) {
        String fileContent = FileUtils.getFileInOneString(file, encoding);

        String searchPattern = Pattern.quote(beginToken) + Pattern.quote(pProperty.getName()) + Pattern.quote(endToken);

        Pattern pattern = Pattern.compile(searchPattern);
        Matcher matcher = pattern.matcher(fileContent);

        return matcher.find();
    }
}