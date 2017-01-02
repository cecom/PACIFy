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

package com.geewhiz.pacify.checks.impl;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.geewhiz.pacify.checks.PMarkerCheck;
import com.geewhiz.pacify.defect.Defect;
import com.geewhiz.pacify.defect.NoPlaceholderInTargetFileDefect;
import com.geewhiz.pacify.defect.PlaceholderNotDefinedDefect;
import com.geewhiz.pacify.managers.EntityManager;
import com.geewhiz.pacify.model.PFile;
import com.geewhiz.pacify.model.PMarker;
import com.geewhiz.pacify.model.PProperty;
import com.geewhiz.pacify.utils.FileUtils;
import com.geewhiz.pacify.utils.RegExpUtils;

public class CheckPlaceholderExistsInTargetFile implements PMarkerCheck {

    public LinkedHashSet<Defect> checkForErrors(EntityManager entityManager, PMarker pMarker) {
        LinkedHashSet<Defect> defects = new LinkedHashSet<Defect>();

        for (PFile pFile : entityManager.getPFilesFrom(pMarker)) {
            // existents of the file is checked in another checker
            if (!pFile.fileExists())
                continue;

            String fileContent = FileUtils.getFileInOneString(pFile.getFile(), pFile.getEncoding());
            if (fileContent == null) {
                continue;
            }

            checkAllPropertiesExistsInTargetFile(defects, pFile, fileContent);
            checkForNotReferencedProperties(defects, pFile, fileContent);
        }

        return defects;
    }

    private void checkForNotReferencedProperties(LinkedHashSet<Defect> defects, PFile pFile, String fileContent) {
        Set<String> notReferencedPlaceHolders = getNotReferencedPlaceHolders(fileContent, pFile);

        for (String notReferencedPlaceHolder : notReferencedPlaceHolders) {
            Defect defect = new PlaceholderNotDefinedDefect(pFile, notReferencedPlaceHolder);
            defects.add(defect);
        }
    }

    private void checkAllPropertiesExistsInTargetFile(LinkedHashSet<Defect> defects, PFile pFile, String fileContent) {
        for (PProperty pProperty : pFile.getPProperties()) {
            boolean exists = doesPropertyExistInFile(fileContent, pProperty);
            if (exists) {
                continue;
            }
            Defect defect = new NoPlaceholderInTargetFileDefect(pProperty);
            defects.add(defect);
        }
    }

    private Set<String> getNotReferencedPlaceHolders(String fileContent, PFile pFile) {
        Set<String> notReferencedPlaceHolder = new TreeSet<String>();

        // are all properties referenced from the marker file?
        Set<String> placeHolders = getAllPlaceHolders(fileContent, pFile);
        for (String placeHolder : placeHolders) {
            boolean foundInMarkerFile = false;

            for (PProperty pProperty : pFile.getPProperties()) {
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

    private Set<String> getAllPlaceHolders(String fileContent, PFile pFile) {
        Pattern pattern = RegExpUtils.getDefaultPattern(pFile.getBeginToken(), pFile.getEndToken());
        Matcher matcher = getPlaceHolderMatcher(fileContent, pattern);

        Set<String> result = new TreeSet<String>();

        while (matcher.find()) {
            String placeHolder = matcher.group(1);
            result.add(placeHolder);
        }

        return result;
    }

    private boolean doesPropertyExistInFile(String fileContent, PProperty pProperty) {
        String beginToken = pProperty.getPFile().getBeginToken();
        String endToken = pProperty.getPFile().getEndToken();

        Pattern pattern = RegExpUtils.getPatternFor(beginToken, endToken, Pattern.quote(pProperty.getName()));
        return getPlaceHolderMatcher(fileContent, pattern).find();
    }

    private Matcher getPlaceHolderMatcher(String fileContent, Pattern pattern) {
        Matcher matcher = pattern.matcher(fileContent);
        return matcher;
    }
}
