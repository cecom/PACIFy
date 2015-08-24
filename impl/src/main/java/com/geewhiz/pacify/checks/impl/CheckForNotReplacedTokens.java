package com.geewhiz.pacify.checks.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.geewhiz.pacify.checks.PMarkerCheck;
import com.geewhiz.pacify.defect.Defect;
import com.geewhiz.pacify.defect.DefectException;
import com.geewhiz.pacify.defect.NotReplacedPropertyDefect;
import com.geewhiz.pacify.model.PArchive;
import com.geewhiz.pacify.model.PFile;
import com.geewhiz.pacify.model.PMarker;
import com.geewhiz.pacify.utils.FileUtils;

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

public class CheckForNotReplacedTokens implements PMarkerCheck {

    @Override
    public List<Defect> checkForErrors(PMarker pMarker) {
        List<Defect> defects = new ArrayList<Defect>();

        checkArchiveEntries(defects, pMarker);
        checkPFileEntries(defects, pMarker);

        return defects;
    }

    private void checkArchiveEntries(List<Defect> defects, PMarker pMarker) {
        for (PArchive pArchive : pMarker.getPArchives()) {
            for (PFile pFile : pArchive.getPFiles()) {
                String fileContent = getFileContent(pMarker, pArchive, pFile);

                String beginToken = pMarker.getBeginTokenFor(pArchive, pFile);
                String endToken = pMarker.getEndTokenFor(pArchive, pFile);

                for (String property : getNotReplacedProperties(fileContent, beginToken, endToken)) {
                    Defect defect = new NotReplacedPropertyDefect(pMarker, pArchive, pFile, property);
                    defects.add(defect);
                }
            }
        }
    }

    private String getFileContent(PMarker pMarker, PArchive pArchive, PFile pFile) {
        String fileContent;
        try {
            fileContent = FileUtils.getFileInOneString(pMarker, pArchive, pFile);
        } catch (DefectException e) {
            // the existence of the file is checked before, so we should not get this exception
            throw new RuntimeException(e);
        }
        return fileContent;
    }

    private void checkPFileEntries(List<Defect> defects, PMarker pMarker) {
        for (PFile pFile : pMarker.getPFiles()) {
            File file = pMarker.getAbsoluteFileFor(pFile);

            String beginToken = pMarker.getBeginTokenFor(pFile);
            String endToken = pMarker.getEndTokenFor(pFile);
            String encoding = pFile.getEncoding();
            String fileContent = FileUtils.getFileInOneString(file, encoding);

            for (String property : getNotReplacedProperties(fileContent, beginToken, endToken)) {
                Defect defect = new NotReplacedPropertyDefect(pMarker, pFile, property);
                defects.add(defect);
            }
        }
    }

    private List<String> getNotReplacedProperties(String fileContent, String beginToken, String endToken) {
        List<String> result = new ArrayList<String>();

        String searchPattern = Pattern.quote(beginToken)
                + "([^" + Pattern.quote(endToken) + "]*)" + Pattern.quote(endToken);

        Pattern pattern = Pattern.compile(searchPattern);
        Matcher matcher = pattern.matcher(fileContent);

        while (matcher.find()) {
            String propertyId = matcher.group(1);
            result.add(propertyId);
        }

        return result;
    }
}
