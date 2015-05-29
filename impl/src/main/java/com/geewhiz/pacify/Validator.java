package com.geewhiz.pacify;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.geewhiz.pacify.checks.Check;
import com.geewhiz.pacify.checks.PMarkerCheck;
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
import com.marzapower.loggable.Log;
import com.marzapower.loggable.Loggable;

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

@Loggable(loggerName = "com.geewhiz.pacify")
public class Validator {

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
        addPMarkerCheck(new CheckTargetFileExist());
        addPMarkerCheck(new CheckPropertyDuplicateDefinedInPacifyFile());
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

        Log.get().info("== Executing Validator [Version=" + Utils.getJarVersion() + "]");

        Log.get().info("== Found [" + entityManager.getPMarkerCount() + "] pacify marker files");
        for (PMarker pMarker : entityManager.getPMarkers()) {
            Log.get().info("   [" + pMarker.getFile().getAbsolutePath() + "]");
        }
        Log.get().info("== Validating ...");

        List<Defect> defects = validateInternal(entityManager);
        DefectUtils.abortIfDefectExists(defects);

        Log.get().info("== Successfully finished");
    }

    public List<Defect> validateInternal(EntityManager entityManager) {
        List<Defect> defects = new ArrayList<Defect>();
        for (Check check : checks) {
            defects.addAll(check.checkForErrors());
        }
        for (PMarker pMarker : entityManager.getPMarkers()) {
            Log.get().debug("   Processing Marker File [" + pMarker.getFile().getAbsolutePath() + "]");
            for (PMarkerCheck pMarkerCheck : pMarkerChecks) {
                Log.get().debug("     Check [" + pMarkerCheck.getClass().getName() + "]");
                defects.addAll(pMarkerCheck.checkForErrors(pMarker));
            }
        }
        return defects;
    }

}
