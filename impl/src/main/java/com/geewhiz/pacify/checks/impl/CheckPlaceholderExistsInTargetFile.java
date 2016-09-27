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
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.geewhiz.pacify.checks.PMarkerCheck;
import com.geewhiz.pacify.defect.Defect;
import com.geewhiz.pacify.defect.DefectException;
import com.geewhiz.pacify.defect.NoPlaceholderInTargetFileDefect;
import com.geewhiz.pacify.defect.PlaceholderNotDefinedDefect;
import com.geewhiz.pacify.model.PArchive;
import com.geewhiz.pacify.model.PFile;
import com.geewhiz.pacify.model.PMarker;
import com.geewhiz.pacify.model.PProperty;
import com.geewhiz.pacify.utils.FileUtils;
import com.geewhiz.pacify.utils.RegExpUtils;

public class CheckPlaceholderExistsInTargetFile implements PMarkerCheck {

    public LinkedHashSet<Defect> checkForErrors(PMarker pMarker) {
        LinkedHashSet<Defect> defects = new LinkedHashSet<Defect>();

        checkArchives(pMarker, defects);
        checkPFiles(pMarker, defects);

        return defects;
    }

    private void checkArchives(PMarker pMarker, LinkedHashSet<Defect> defects) {
        for (PArchive pArchive : pMarker.getPArchives()) {
            for (PFile pFile : pArchive.getPFiles()) {
                String fileContent = null;
                try {
                    fileContent = FileUtils.getFileInOneString(pMarker, pArchive, pFile);
                } catch (DefectException e) {
                    // this is checked from another checker, so we dont throw it.
                    continue;
                }

                String beginToken = pMarker.getBeginTokenFor(pArchive, pFile);
                String endToken = pMarker.getEndTokenFor(pArchive, pFile);

                // check for properties which are referenced from the marker file
                for (PProperty pProperty : pFile.getPProperties()) {
                    boolean exists = doesPropertyExistInFile(fileContent, pProperty, pFile.getEncoding(), beginToken, endToken);
                    if (exists) {
                        continue;
                    }
                    Defect defect = new NoPlaceholderInTargetFileDefect(pMarker, pArchive, pFile, pProperty);
                    defects.add(defect);
                }

                // are all properties referenced from the marker file?
                Set<String> notReferencedPlaceHolders = getNotReferencedPlaceHolders(fileContent, pFile.getEncoding(), pFile.getPProperties(), beginToken,
                        endToken);

                for (String notReferencedPlaceHolder : notReferencedPlaceHolders) {
                    Defect defect = new PlaceholderNotDefinedDefect(pMarker, pArchive, pFile, notReferencedPlaceHolder);
                    defects.add(defect);
                }
            }

        }
    }

    private void checkPFiles(PMarker pMarker, LinkedHashSet<Defect> defects) {
        for (PFile pFile : pMarker.getPFiles()) {
            File file = pMarker.getAbsoluteFileFor(pFile);
            if (!file.exists()) {
                // is checked before
                continue;
            }

            String beginToken = pMarker.getBeginTokenFor(pFile);
            String endToken = pMarker.getEndTokenFor(pFile);

            // check for properties which are referenced from the marker file
            String fileContent = FileUtils.getFileInOneString(file, pFile.getEncoding());
            for (PProperty pProperty : pFile.getPProperties()) {
                boolean exists = doesPropertyExistInFile(fileContent, pProperty, pFile.getEncoding(), beginToken, endToken);
                if (exists) {
                    continue;
                }
                Defect defect = new NoPlaceholderInTargetFileDefect(pMarker, pFile, pProperty);
                defects.add(defect);
            }

            // are all properties referenced from the marker file?
            Set<String> notReferencedPlaceHolders = getNotReferencedPlaceHolders(fileContent, pFile.getEncoding(), pFile.getPProperties(), beginToken,
                    endToken);

            for (String notReferencedPlaceHolder : notReferencedPlaceHolders) {
                Defect defect = new PlaceholderNotDefinedDefect(pMarker, pFile, notReferencedPlaceHolder);
                defects.add(defect);
            }
        }
    }

    private Set<String> getNotReferencedPlaceHolders(String fileContent, String encoding, List<PProperty> pProperties, String beginToken, String endToken) {

        Set<String> notReferencedPlaceHolder = new TreeSet<String>();

        // are all properties referenced from the marker file?
        Set<String> placeHolders = getAllPlaceHolders(fileContent, encoding, beginToken, endToken);
        for (String placeHolder : placeHolders) {
            boolean foundInMarkerFile = false;

            for (PProperty pProperty : pProperties) {
                if (placeHolder.equals(pProperty.getName())) {
                    foundInMarkerFile = true;
                    break;
                }
            }

            if (!foundInMarkerFile) {
                notReferencedPlaceHolder.add(placeHolder);
            }
        }
        return notReferencedPlaceHolder;
    }

    private Set<String> getAllPlaceHolders(String fileContent, String encoding, String beginToken, String endToken) {
        Pattern pattern = RegExpUtils.getDefaultPattern(beginToken, endToken);
        Matcher matcher = getPlaceHolderMatcher(fileContent, pattern, encoding, beginToken, endToken);

        Set<String> result = new TreeSet<String>();

        while (matcher.find()) {
            String placeHolder = matcher.group(1);
            result.add(placeHolder);
        }

        return result;
    }

    private boolean doesPropertyExistInFile(String fileContent, PProperty pProperty, String encoding, String beginToken, String endToken) {
        Pattern pattern = RegExpUtils.getPatternFor(beginToken, endToken, Pattern.quote(pProperty.getName()));
        return getPlaceHolderMatcher(fileContent, pattern, encoding, beginToken, endToken).find();
    }

    private Matcher getPlaceHolderMatcher(String fileContent, Pattern pattern, String encoding, String beginToken, String endToken) {
        Matcher matcher = pattern.matcher(fileContent);
        return matcher;
    }
}