package com.geewhiz.pacify.filter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.tools.ant.types.FilterSet;
import org.apache.tools.ant.types.FilterSetCollection;
import org.apache.tools.ant.util.FileUtils;

import com.geewhiz.pacify.defect.Defect;

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

    @Override
    public List<Defect> filter(Map<String, String> propertyValues, String beginToken, String endToken, File file, String encoding) {
        FilterSetCollection filterSetCollection = getFilterSetCollection(propertyValues, beginToken, endToken);

        try {
            File tmpFile = File.createTempFile(file.getName(), "tmp", file.getParentFile());

            FileUtils.getFileUtils().copyFile(file, tmpFile, filterSetCollection, true, true, encoding);
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

        return new ArrayList<Defect>();
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
}
