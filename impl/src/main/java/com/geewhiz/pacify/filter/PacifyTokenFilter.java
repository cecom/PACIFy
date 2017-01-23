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

package com.geewhiz.pacify.filter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.tools.ant.types.FilterSet;
import org.apache.tools.ant.types.FilterSetCollection;
import org.apache.tools.ant.util.FileUtils;

import com.geewhiz.pacify.defect.Defect;
import com.geewhiz.pacify.defect.NoPlaceholderInTargetFileDefect;
import com.geewhiz.pacify.defect.NotReplacedPropertyDefect;
import com.geewhiz.pacify.defect.PlaceholderNotDefinedDefect;
import com.geewhiz.pacify.model.PFile;
import com.geewhiz.pacify.model.PProperty;
import com.geewhiz.pacify.utils.RegExpUtils;

public class PacifyTokenFilter implements PacifyFilter {

    @Override
    public LinkedHashSet<Defect> filter(PFile pFile, Map<String, String> propertyValues) {
        FilterSetCollection filterSetCollection = getFilterSetCollection(propertyValues, pFile.getBeginToken(), pFile.getEndToken());

        try {
            File fileToFilter = pFile.getFile();
            File tmpFile = com.geewhiz.pacify.utils.FileUtils.createEmptyFileWithSamePermissions(fileToFilter);

            FileUtils.getFileUtils().copyFile(fileToFilter, tmpFile, filterSetCollection, true, true, pFile.getEncoding());
            if (!fileToFilter.delete()) {
                throw new RuntimeException("Couldn't delete file [" + fileToFilter.getPath() + "]... Aborting!");
            }
            if (!tmpFile.renameTo(fileToFilter)) {
                throw new RuntimeException("Couldn't rename filtered file from [" + tmpFile.getPath() + "] to [" + fileToFilter.getPath() + "]... Aborting!");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return new LinkedHashSet<Defect>();
    }

    private FilterSetCollection getFilterSetCollection(Map<String, String> propertyValues, String beginToken, String endToken) {
        FilterSet filterSet = getFilterSet(propertyValues, beginToken, endToken);

        FilterSetCollection executionFilters = new FilterSetCollection();
        executionFilters.addFilterSet(filterSet);

        return executionFilters;
    }

    private FilterSet getFilterSet(Map<String, String> propertyValues, String beginToken, String endToken) {
        FilterSet filterSet = new FilterSet();

        filterSet.setBeginToken(beginToken);
        filterSet.setEndToken(endToken);

        for (Entry<String, String> entry : propertyValues.entrySet()) {
            filterSet.addFilter(entry.getKey(), entry.getValue());
        }
        return filterSet;
    }

    @Override
    public LinkedHashSet<Defect> checkForNotReplacedTokens(PFile pFile) {
        LinkedHashSet<Defect> defects = new LinkedHashSet<Defect>();

        String fileContent = com.geewhiz.pacify.utils.FileUtils.getFileInOneString(pFile.getFile(), pFile.getEncoding());
        if (fileContent == null) {
            return defects;
        }

        for (String property : getNotReplacedProperties(fileContent, pFile.getBeginToken(), pFile.getEndToken())) {
            Defect defect = new NotReplacedPropertyDefect(pFile, property);
            defects.add(defect);
        }
        return defects;
    }

    private List<String> getNotReplacedProperties(String fileContent, String beginToken, String endToken) {
        List<String> result = new ArrayList<String>();

        Pattern pattern = RegExpUtils.getDefaultPattern(beginToken, endToken);
        Matcher matcher = pattern.matcher(fileContent);

        while (matcher.find()) {
            String propertyId = matcher.group(1);
            result.add(propertyId);
        }

        return result;
    }

    @Override
    public LinkedHashSet<Defect> checkPlaceHolderExists(PFile pFile) {
        LinkedHashSet<Defect> defects = new LinkedHashSet<Defect>();

        String fileContent = com.geewhiz.pacify.utils.FileUtils.getFileInOneString(pFile.getFile(), pFile.getEncoding());
        if (fileContent == null) {
            return defects;
        }

        checkAllPropertiesExistsInTargetFile(defects, pFile, fileContent);
        checkForNotReferencedProperties(defects, pFile, fileContent);

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
