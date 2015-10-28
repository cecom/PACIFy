package com.geewhiz.pacify;

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
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashSet;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.geewhiz.pacify.defect.Defect;
import com.geewhiz.pacify.defect.DefectException;
import com.geewhiz.pacify.defect.DefectMessage;
import com.geewhiz.pacify.managers.EntityManager;
import com.geewhiz.pacify.managers.FilterManager;
import com.geewhiz.pacify.managers.PropertyResolveManager;
import com.geewhiz.pacify.model.PMarker;
import com.geewhiz.pacify.utils.DefectUtils;
import com.geewhiz.pacify.utils.Utils;
import com.google.inject.Inject;

public class Replacer {

    private Logger                 logger = LogManager.getLogger(Replacer.class.getName());

    private PropertyResolveManager propertyResolveManager;
    private File                   packagePath;
    private File                   copyDestination;

    @Inject
    public Replacer(PropertyResolveManager propertyResolveManager) {
        this.propertyResolveManager = propertyResolveManager;
    }

    public void execute() {
        logger.info("== Executing Replacer [Version={}]", Utils.getJarVersion());
        logger.info("   [PackagePath={}]", getPackagePath().getAbsolutePath());

        File pathToConfigure = getPathToConfigure();

        EntityManager entityManager = new EntityManager(pathToConfigure);

        logger.info("== Found [{}] pacify marker files", entityManager.getPMarkerCount());
        logger.info("== Validating...");

        LinkedHashSet<Defect> defects = createValidator().validateInternal(entityManager);
        DefectUtils.abortIfDefectExists(defects);

        logger.info("== Replacing...");
        defects = doReplacement(entityManager);
        DefectUtils.abortIfDefectExists(defects);

        logger.info("== Successfully finished");
    }

    private File getPathToConfigure() {
        if (copyDestination == null) {
            return getPackagePath();
        }

        File result = null;
        logger.info("   [Destination={}]", getCopyDestination().getAbsolutePath());
        try {
            result = createCopy();
        } catch (DefectException e) {
            DefectUtils.abortIfDefectExists(new LinkedHashSet<Defect>(Arrays.asList(e)));
        }

        return result;
    }

    public File getPackagePath() {
        return packagePath;
    }

    public void setPackagePath(File packagePath) {
        this.packagePath = packagePath;
    }

    public File getCopyDestination() {
        return copyDestination;
    }

    public void setCopyDestination(File copyDestination) {
        this.copyDestination = copyDestination;
    }

    private File createCopy() throws DefectException {
        try {
            if (getCopyDestination().exists()) {
                if (!getCopyDestination().isDirectory()) {
                    throw new DefectMessage("Destination directory [" + getCopyDestination().getAbsolutePath() + "] is not a directory.");
                }
                if (getCopyDestination().list().length > 0) {
                    throw new DefectMessage("Destination directory [" + getCopyDestination().getAbsolutePath() + "] is not empty.");
                }
                if (!getCopyDestination().canWrite()) {
                    throw new DefectMessage("Destination directory [" + getCopyDestination().getAbsolutePath() + "] is not writable.");
                }
            }
            FileUtils.copyDirectory(getPackagePath(), getCopyDestination());
            return getCopyDestination();
        } catch (IOException e) {
            logger.debug(e);
            throw new DefectMessage("Error while copy [" + getPackagePath().getAbsolutePath() + "] to [" + getCopyDestination().getAbsolutePath() + "].");
        }
    }

    public LinkedHashSet<Defect> doReplacement(EntityManager entityManager) {
        LinkedHashSet<Defect> defects = new LinkedHashSet<Defect>();
        for (PMarker pMarker : entityManager.getPMarkers()) {
            logger.info("   Processing Marker File [{}],", pMarker.getFile().getAbsolutePath());
            FilterManager filterManager = new FilterManager(propertyResolveManager,
                    pMarker);
            defects.addAll(filterManager.doFilter());
        }
        return defects;
    }

    private Validator createValidator() {
        Validator validator = new Validator(propertyResolveManager);
        validator.setPackagePath(packagePath);
        validator.enableMarkerFileChecks();
        validator.enablePropertyResolveChecks();
        return validator;
    }
}
