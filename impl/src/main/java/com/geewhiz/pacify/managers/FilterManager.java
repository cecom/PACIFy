package com.geewhiz.pacify.managers;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.NotImplementedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.geewhiz.pacify.checks.impl.CheckForNotReplacedTokens;
import com.geewhiz.pacify.defect.Defect;
import com.geewhiz.pacify.defect.DefectException;
import com.geewhiz.pacify.filter.PacifyFilter;
import com.geewhiz.pacify.model.PArchive;
import com.geewhiz.pacify.model.PFile;
import com.geewhiz.pacify.model.PMarker;
import com.geewhiz.pacify.model.PProperty;
import com.geewhiz.pacify.utils.FileUtils;
import com.geewhiz.pacify.utils.Utils;

public class FilterManager {

    private Logger                 logger = LogManager.getLogger(FilterManager.class.getName());

    private PropertyResolveManager propertyResolveManager;
    private PMarker                pMarker;

    public FilterManager(PropertyResolveManager propertyResolveManager, PMarker pMarker) {
        this.propertyResolveManager = propertyResolveManager;
        this.pMarker = pMarker;
    }

    public List<Defect> doFilter() {
        List<Defect> defects = new ArrayList<Defect>();

        for (Object entry : pMarker.getFilesAndArchives()) {
            if (entry instanceof PFile) {
                defects.addAll(filterPFile((PFile) entry));
            } else if (entry instanceof PArchive) {
                defects.addAll(filterPArchive((PArchive) entry));
            } else {
                throw new NotImplementedException("Filter implementation for " + entry.getClass().getName() + " not implemented.");
            }
        }

        CheckForNotReplacedTokens checker = new CheckForNotReplacedTokens();
        defects.addAll(checker.checkForErrors(pMarker));

        if (defects.isEmpty()) {
            pMarker.getFile().delete();
        }
        return defects;
    }

    private List<Defect> filterPFile(PFile pFile) {
        logger.debug("     Filtering [{}] using encoding [{}] and filter [{}]", pMarker.getAbsoluteFileFor(pFile).getAbsolutePath(), pFile.getEncoding(),
                pFile.getFilterClass());

        File file = pMarker.getAbsoluteFileFor(pFile);
        PacifyFilter pacifyFilter = getFilterForPFile(pFile);

        Map<String, String> propertyValues = getPropertyValues(pFile);
        String beginToken = pMarker.getBeginTokenFor(pFile);
        String endToken = pMarker.getEndTokenFor(pFile);
        String encoding = pFile.getEncoding();

        List<Defect> defects = new ArrayList<Defect>();
        defects.addAll(pacifyFilter.filter(propertyValues, beginToken, endToken, file, encoding));

        return defects;
    }

    private List<Defect> filterPArchive(PArchive pArchive) {
        List<Defect> defects = new ArrayList<Defect>();

        for (PFile pFile : pArchive.getPFiles()) {
            File extractedFile = extractFile(pArchive, pFile);
            PacifyFilter pacifyFilter = getFilterForPFile(pArchive, pFile);

            Map<String, String> propertyValues = getPropertyValues(pFile);
            String beginToken = pMarker.getBeginTokenFor(pArchive, pFile);
            String endToken = pMarker.getEndTokenFor(pArchive, pFile);
            String encoding = pFile.getEncoding();

            defects.addAll(pacifyFilter.filter(propertyValues, beginToken, endToken, extractedFile, encoding));

            FileUtils.replaceFileInArchive(pMarker, pArchive, pFile, extractedFile);
        }
        return defects;
    }

    private Map<String, String> getPropertyValues(PFile pFile) {
        HashMap<String, String> result = new HashMap<String, String>();

        for (PProperty pProperty : pFile.getPProperties()) {
            String propertyName = pProperty.getName();
            String propertyValue = propertyResolveManager.getPropertyValue(pProperty);
            result.put(propertyName, propertyValue);
        }

        return result;
    }

    private PacifyFilter getFilterForPFile(PFile pFile) {
        return getFilterForPFile(null, pFile);
    }

    private PacifyFilter getFilterForPFile(PArchive pArchive, PFile pFile) {
        try {
            return Utils.getPacifyFilter(pMarker, pArchive, pFile);
        } catch (DefectException e) {
            // is checked before, so we should not get this exception here.
            throw new RuntimeException(e);
        }
    }

    private File extractFile(PArchive pArchive, PFile pFile) {
        try {
            return FileUtils.extractFile(pMarker, pArchive, pFile);
        } catch (DefectException e) {
            // Existence is checked before, so we should not get this exception here
            throw new RuntimeException(e);
        }
    }
}
