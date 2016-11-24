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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.geewhiz.pacify.checks.impl.CheckForNotReplacedTokens;
import com.geewhiz.pacify.defect.Defect;
import com.geewhiz.pacify.defect.DefectException;
import com.geewhiz.pacify.defect.PropertyNotDefinedInResolverDefect;
import com.geewhiz.pacify.exceptions.PropertyNotFoundRuntimeException;
import com.geewhiz.pacify.filter.PacifyFilter;
import com.geewhiz.pacify.model.PFile;
import com.geewhiz.pacify.model.PMarker;
import com.geewhiz.pacify.model.PProperty;
import com.geewhiz.pacify.utils.Utils;

public class FilterManager {

    private Logger                 logger = LogManager.getLogger(FilterManager.class.getName());

    private EntityManager          entityManager;
    private PropertyResolveManager propertyResolveManager;

    public FilterManager(EntityManager entityManager, PropertyResolveManager propertyResolveManager) {
        this.entityManager = entityManager;
        this.propertyResolveManager = propertyResolveManager;
    }

    public LinkedHashSet<Defect> doFilter() {
        LinkedHashSet<Defect> defects = new LinkedHashSet<Defect>();

        for (PMarker pMarker : entityManager.getPMarkers()) {
            logger.info("   Processing Marker File [{}],", pMarker.getFile().getAbsolutePath());

            for (PFile pFile : entityManager.getPFilesFrom(pMarker)) {
                defects.addAll(filterPFile(pFile));
            }

            CheckForNotReplacedTokens checker = new CheckForNotReplacedTokens();
            defects.addAll(checker.checkForErrors(entityManager, pMarker));

            if (defects.isEmpty()) {
                pMarker.getFile().delete();
                entityManager.postProcessPMarker(pMarker);
            }
        }
        
        return defects;
    }

    private LinkedHashSet<Defect> filterPFile(PFile pFile) {
        logger.info("      Customize File [{}]", pFile.getPUri());
        logger.debug("          Filtering [{}] using encoding [{}] and filter [{}]", pFile.getPUri(), pFile.getEncoding(), pFile.getFilterClass());

        File fileToFilter = pFile.getFile();
        PacifyFilter pacifyFilter = getFilterForPFile(pFile);

        Map<String, String> propertyValues = new HashMap<String, String>();
        LinkedHashSet<Defect> defects = fillPropertyValuesFor(propertyValues, pFile);

        String beginToken = pFile.getBeginToken();
        String endToken = pFile.getEndToken();
        String encoding = pFile.getEncoding();

        defects.addAll(pacifyFilter.filter(propertyValues, beginToken, endToken, fileToFilter, encoding));

        fileToFilter.setLastModified(System.currentTimeMillis());

        logger.info("          [{}] placeholders replaced.", pFile.getPProperties().size());

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
                Defect defect = new PropertyNotDefinedInResolverDefect(pProperty, propertyResolveManager.toString());
                defects.add(defect);
                continue;
            }
            propertyValues.put(propertyName, propertyValue);
        }

        return defects;
    }

    private PacifyFilter getFilterForPFile(PFile pFile) {
        try {
            return Utils.getPacifyFilter(pFile);
        } catch (DefectException e) {
            // is checked before, so we should not get this exception here.
            throw new RuntimeException(e);
        }
    }

}
