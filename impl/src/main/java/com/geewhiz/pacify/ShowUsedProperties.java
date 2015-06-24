package com.geewhiz.pacify;

import java.io.File;
import java.util.Set;
import java.util.TreeSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.geewhiz.pacify.managers.EntityManager;
import com.geewhiz.pacify.model.PMarker;
import com.geewhiz.pacify.model.PProperty;
import com.geewhiz.pacify.utils.Utils;

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

public class ShowUsedProperties {

    private Logger logger = LogManager.getLogger(ShowUsedProperties.class.getName());

    File           packagePath;

    public File getPackagePath() {
        return packagePath;
    }

    public void setPackagePath(File packagePath) {
        this.packagePath = packagePath;
    }

    public void execute() {
        EntityManager entityManager = new EntityManager(getPackagePath());

        logger.info("== Executing ShowUsedProperties [Version={}]", Utils.getJarVersion());

        logger.info("== Found [{}] pacify marker files", entityManager.getPMarkerCount());

        logger.info("== Getting Properties...");

        Set<String> allUsedProperties = new TreeSet<String>();
        for (PMarker pMarker : entityManager.getPMarkers()) {
            logger.info("   [{}]", pMarker.getFile().getAbsolutePath());

            for (PProperty pProperty : pMarker.getProperties()) {
                allUsedProperties.add(pProperty.getName());
            }
        }

        for (String usedProperty : allUsedProperties) {
            System.out.println(usedProperty);
        }

        logger.info("== Successfully finished");
    }
}
