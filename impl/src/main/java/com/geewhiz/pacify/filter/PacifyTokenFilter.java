package com.geewhiz.pacify.filter;

import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tools.ant.types.FilterSet;
import org.apache.tools.ant.types.FilterSetCollection;
import org.apache.tools.ant.util.FileUtils;

import com.geewhiz.pacify.managers.PropertyResolveManager;
import com.geewhiz.pacify.model.PFile;
import com.geewhiz.pacify.model.PMarker;
import com.geewhiz.pacify.model.PProperty;

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

public class PacifyTokenFilter implements PacifyFilter {

    private Logger logger = LogManager.getLogger(PacifyTokenFilter.class.getName());

    @Override
    public void filter(PropertyResolveManager propertyResolveManager, PMarker pMarker, PFile pFile) {
        File file = pMarker.getAbsoluteFileFor(pFile);
        FilterSetCollection filterSetCollection = getFilterSetCollection(propertyResolveManager, pMarker, pFile);

        File tmpFile = new File(file.getParentFile(), file.getName() + "_tmp");

        try {
            FileUtils.getFileUtils().copyFile(file, tmpFile, filterSetCollection, true, true, pFile.getEncoding());
            if (!file.delete()) {
                throw new RuntimeException("Couldn't delete file [" + file.getPath() + "]... Aborting!");
            }
            if (!tmpFile.renameTo(file)) {
                throw new RuntimeException("Couldn't rename filtered file from [" + tmpFile.getPath() + "] to ["
                        + file.getPath() + "]... Aborting!");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private FilterSetCollection getFilterSetCollection(PropertyResolveManager propertyResolveManager, PMarker pMarker, PFile pFile) {
        FilterSet filterSet = getFilterSet(propertyResolveManager, pMarker, pFile);

        FilterSetCollection executionFilters = new FilterSetCollection();
        executionFilters.addFilterSet(filterSet);

        return executionFilters;
    }

    private FilterSet getFilterSet(PropertyResolveManager propertyResolveManager, PMarker pMarker, PFile pFile) {
        FilterSet filterSet = new FilterSet();

        filterSet.setBeginToken(pMarker.getBeginTokenFor(pFile));
        filterSet.setEndToken(pMarker.getEndTokenFor(pFile));

        for (PProperty pProperty : pFile.getPProperties()) {
            String propertyName = pProperty.getName();
            String propertyValue = propertyResolveManager.getPropertyValue(pProperty);

            filterSet.addFilter(propertyName, propertyValue);
        }
        return filterSet;
    }
}
