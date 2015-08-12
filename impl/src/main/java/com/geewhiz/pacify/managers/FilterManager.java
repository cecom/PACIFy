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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.geewhiz.pacify.checks.impl.CheckForNotReplacedTokens;
import com.geewhiz.pacify.defect.Defect;
import com.geewhiz.pacify.defect.FilterNotFoundDefect;
import com.geewhiz.pacify.filter.PacifyFilter;
import com.geewhiz.pacify.model.PFile;
import com.geewhiz.pacify.model.PMarker;
import com.geewhiz.pacify.model.PProperty;

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

        for (PFile pFile : pMarker.getPFiles()) {
            logger.debug("     Filtering [{}] using encoding [{}] and filter [{}]", pMarker.getAbsoluteFileFor(pFile).getAbsolutePath(), pFile.getEncoding(),
                    pFile.getFilterClass());

            PacifyFilter pacifyFilter = getFilterForPFile(pFile);

            if (pacifyFilter == null) {
                defects.add(new FilterNotFoundDefect(pMarker, pFile));
            } else {
                Map<String, String> propertyValues = getPropertyValues(pFile);
                String beginToken = pMarker.getBeginTokenFor(pFile);
                String endToken = pMarker.getEndTokenFor(pFile);
                File file = pMarker.getAbsoluteFileFor(pFile);
                String encoding = pFile.getEncoding();

                defects.addAll(pacifyFilter.filter(propertyValues, beginToken, endToken, file, encoding));

                CheckForNotReplacedTokens checker = new CheckForNotReplacedTokens();
                defects.addAll(checker.checkForErrors(pMarker, pFile));
            }
        }

        if (defects.isEmpty()) {
            pMarker.getFile().delete();
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
        String filterClass = pFile.getFilterClass();

        try {
            return (PacifyFilter) Class.forName(filterClass).newInstance();
        } catch (Exception e) {
            logger.debug("Error while instantiate filter class [" + filterClass + "]", e);
            return null;
        }
    }
}
