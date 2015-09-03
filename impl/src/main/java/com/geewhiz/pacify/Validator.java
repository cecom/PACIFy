package com.geewhiz.pacify;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.geewhiz.pacify.checks.Check;
import com.geewhiz.pacify.checks.PMarkerCheck;
import com.geewhiz.pacify.checks.impl.CheckArchiveDuplicateDefinedInPacifyFile;
import com.geewhiz.pacify.checks.impl.CheckCorrectArchiveType;
import com.geewhiz.pacify.checks.impl.CheckCorrectPacifyFilter;
import com.geewhiz.pacify.checks.impl.CheckFileDuplicateDefinedInPacifyFile;
import com.geewhiz.pacify.checks.impl.CheckPlaceholderExistsInTargetFile;
import com.geewhiz.pacify.checks.impl.CheckPropertyDuplicateDefinedInPacifyFile;
import com.geewhiz.pacify.checks.impl.CheckPropertyDuplicateInPropertyFile;
import com.geewhiz.pacify.checks.impl.CheckPropertyExists;
import com.geewhiz.pacify.checks.impl.CheckTargetFileExist;
import com.geewhiz.pacify.defect.Defect;
import com.geewhiz.pacify.managers.EntityManager;
import com.geewhiz.pacify.managers.PropertyResolveManager;
import com.geewhiz.pacify.model.PMarker;
import com.geewhiz.pacify.utils.DefectUtils;
import com.geewhiz.pacify.utils.Utils;
import com.google.inject.Inject;

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

public class Validator {

    private Logger                 logger        = LogManager.getLogger(Validator.class.getName());

    File                           packagePath;
    List<Check>                    checks        = new ArrayList<Check>();
    List<PMarkerCheck>             pMarkerChecks = new ArrayList<PMarkerCheck>();

    private PropertyResolveManager propertyResolveManager;

    @Inject
    public Validator(PropertyResolveManager propertyResolveManager) {
        this.propertyResolveManager = propertyResolveManager;
    }

    public void enablePropertyResolveChecks() {
        addCheck(new CheckPropertyDuplicateInPropertyFile(propertyResolveManager));
        addPMarkerCheck(new CheckPropertyExists(propertyResolveManager));
    }

    public void enableMarkerFileChecks() {
        addPMarkerCheck(new CheckArchiveDuplicateDefinedInPacifyFile());
        addPMarkerCheck(new CheckCorrectArchiveType());
        addPMarkerCheck(new CheckFileDuplicateDefinedInPacifyFile());
        addPMarkerCheck(new CheckPropertyDuplicateDefinedInPacifyFile());
        addPMarkerCheck(new CheckCorrectPacifyFilter());
        addPMarkerCheck(new CheckTargetFileExist());
        addPMarkerCheck(new CheckPlaceholderExistsInTargetFile());
    }

    public void addCheck(Check check) {
        checks.add(check);
    }

    public void addPMarkerCheck(PMarkerCheck check) {
        pMarkerChecks.add(check);
    }

    public List<Check> getChecks() {
        return checks;
    }

    public List<PMarkerCheck> getPMarkerChecks() {
        return pMarkerChecks;
    }

    public File getPackagePath() {
        return packagePath;
    }

    public void setPackagePath(File packagePath) {
        this.packagePath = packagePath;
    }

    public void execute() {
        EntityManager entityManager = new EntityManager(getPackagePath());

        logger.info("== Executing Validator [Version={}]", Utils.getJarVersion());

        logger.info("== Found [{}] pacify marker files", entityManager.getPMarkerCount());
        logger.info("== Validating ...");

        List<Defect> defects = validateInternal(entityManager);
        DefectUtils.abortIfDefectExists(defects);

        logger.info("== Successfully finished");
    }

    public List<Defect> validateInternal(EntityManager entityManager) {
        List<Defect> defects = new ArrayList<Defect>();

        defects.addAll(entityManager.initialize());

        for (Check check : checks) {
            logger.debug("     Check [{}]", check.getClass().getName());
            defects.addAll(check.checkForErrors());
        }

        for (PMarker pMarker : entityManager.getPMarkers()) {
            logger.debug("   Processing Marker File [{}]", pMarker.getFile().getAbsolutePath());
            for (PMarkerCheck pMarkerCheck : pMarkerChecks) {
                logger.debug("     Check [{}]", pMarkerCheck.getClass().getName());
                defects.addAll(pMarkerCheck.checkForErrors(pMarker));
            }
        }
        return defects;
    }

}
