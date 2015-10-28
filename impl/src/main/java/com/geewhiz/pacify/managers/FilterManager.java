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
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.NotImplementedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.geewhiz.pacify.checks.impl.CheckForNotReplacedTokens;
import com.geewhiz.pacify.defect.ArchiveDefect;
import com.geewhiz.pacify.defect.Defect;
import com.geewhiz.pacify.defect.DefectException;
import com.geewhiz.pacify.defect.PropertyNotDefinedDefect;
import com.geewhiz.pacify.exceptions.PropertyNotFoundRuntimeException;
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

    public LinkedHashSet<Defect> doFilter() {
        LinkedHashSet<Defect> defects = new LinkedHashSet<Defect>();

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

    private LinkedHashSet<Defect> filterPFile(PFile pFile) {
        logger.info("      Customize File [{}]", pFile.getRelativePath());
        logger.debug("          Filtering [{}] using encoding [{}] and filter [{}]", pMarker.getAbsoluteFileFor(pFile).getAbsolutePath(), pFile.getEncoding(),
                pFile.getFilterClass());

        File fileToFilter = pMarker.getAbsoluteFileFor(pFile);
        PacifyFilter pacifyFilter = getFilterForPFile(pFile);

        Map<String, String> propertyValues = new HashMap<String, String>();
        LinkedHashSet<Defect> defects = fillPropertyValuesFor(propertyValues, pFile);

        String beginToken = pMarker.getBeginTokenFor(pFile);
        String endToken = pMarker.getEndTokenFor(pFile);
        String encoding = pFile.getEncoding();

        defects.addAll(pacifyFilter.filter(propertyValues, beginToken, endToken, fileToFilter, encoding));
        logger.info("          [{}] placeholders replaced.", pFile.getPProperties().size());

        return defects;
    }

    private LinkedHashSet<Defect> filterPArchive(PArchive pArchive) {
        logger.info("      Customize Archive [{}]", pArchive.getRelativePath());

        LinkedHashSet<Defect> defects = new LinkedHashSet<Defect>();

        Map<PFile, File> replaceFiles = new HashMap<PFile, File>();

        for (PFile pFile : pArchive.getPFiles()) {
            logger.info("         Customize File [{}]", pFile.getRelativePath());
            logger.debug("             Filtering [{}] in archive [{}] using encoding [{}] and filter [{}]", pFile.getRelativePath(),
                    pMarker.getAbsoluteFileFor(pArchive).getAbsolutePath(), pFile.getEncoding(),
                    pFile.getFilterClass());

            File fileToFilter = extractFile(pArchive, pFile);
            PacifyFilter pacifyFilter = getFilterForPFile(pArchive, pFile);

            Map<String, String> propertyValues = new HashMap<String, String>();
            LinkedHashSet<Defect> propertyValueDefects = fillPropertyValuesFor(propertyValues, pFile);
            if (propertyValueDefects.size() > 0) {
                return propertyValueDefects;
            }

            String beginToken = pMarker.getBeginTokenFor(pArchive, pFile);
            String endToken = pMarker.getEndTokenFor(pArchive, pFile);
            String encoding = pFile.getEncoding();

            defects.addAll(pacifyFilter.filter(propertyValues, beginToken, endToken, fileToFilter, encoding));

            replaceFiles.put(pFile, fileToFilter);
            logger.info("             [{}] placeholders replaced.", pFile.getPProperties().size());
        }

        try {
            FileUtils.replaceFilesInArchive(pMarker, pArchive, replaceFiles);
        } catch (ArchiveDefect e) {
            defects.add(e);
        }

        for (Entry<PFile, File> entry : replaceFiles.entrySet()) {
            entry.getValue().delete();
        }

        return defects;
    }

    private LinkedHashSet<Defect> fillPropertyValuesFor(Map<String, String> propertyValues, PFile pFile) {
        LinkedHashSet<Defect> defects = new LinkedHashSet<Defect>();

        for (PProperty pProperty : pFile.getPProperties()) {
            String propertyName = pProperty.getName();
            String propertyValue = null;
            try {
                propertyValue = propertyResolveManager.getPropertyValue(pProperty);
            } catch (PropertyNotFoundRuntimeException e) {
                Defect defect = new PropertyNotDefinedDefect(pMarker, pFile, pProperty, propertyResolveManager.toString());
                defects.add(defect);
                continue;
            }
            propertyValues.put(propertyName, propertyValue);
        }

        return defects;
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
