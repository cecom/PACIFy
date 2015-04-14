package com.geewhiz.pacify.mavenplugin;

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
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;

import com.geewhiz.pacify.checker.CheckPropertyDuplicateInPropertyFile;
import com.geewhiz.pacify.checker.CheckPropertyExists;
import com.geewhiz.pacify.defect.Defect;
import com.geewhiz.pacify.model.EntityManager;
import com.geewhiz.pacify.model.Pacify;
import com.geewhiz.pacify.property.FilePropertyContainer;
import com.geewhiz.pacify.property.PropertyContainer;

/**
 * @goal checkDeliveryProperties
 * @phase install
 */
public class CheckDeliveryPropertyFiles extends BaseMojo {

    /**
     * @parameter default-value="${project.build.outputDirectory}"
     * @required
     */
    private File pfListStartPath;

    /**
     * which files should be checked? its a comma separated list
     * 
     * @parameter
     * @required
     */
    private String propertyFiles;

    /**
     * In which jar is the propertyFile contained?
     * 
     * @parameter
     * @required
     */
    protected String propertyFileArtifact;

    @Override
    protected void executePFList() throws MojoExecutionException {
        if (!pfListStartPath.exists()) {
            File outputDirectory = new File(project.getModel().getBuild().getOutputDirectory());
            if (pfListStartPath.equals(outputDirectory)) {
                getLog().debug("Directory [" + pfListStartPath.getAbsolutePath() + "] does  not exists. Nothing to do.");
                return; // if it is a maven project which doesn't have a target folder, do nothing.
            }
            throw new MojoExecutionException("The folder [" + pfListStartPath.getAbsolutePath() + "] does not exist.");
        }

        EntityManager pfEntityManager = new EntityManager(pfListStartPath);
        if (pfEntityManager.getPFListCount() == 0) {
            getLog().info("No pflist files found. Nothing to check.");
            return;
        }

        getLog().info("Found [" + pfEntityManager.getPFListCount() + "] PFList Files...");

        List<Defect> defects = new ArrayList<Defect>();
        for (String propertyFile : propertyFiles.split(",")) {
            getLog().info("Checking property file [" + propertyFile + "] ...");
            defects.addAll(checkPropertyFile(pfEntityManager, propertyFile));
        }

        if (defects.isEmpty()) {
            return;
        }

        getLog().error("==== !!!!!! We got Errors !!!!! ...");
        for (Defect defect : defects) {
            getLog().error(defect.getDefectMessage());
        }
        throw new MojoExecutionException("We got errors... Aborting!");
    }

    private List<Defect> checkPropertyFile(EntityManager pfEntityManager, String propertyFile)
            throws MojoExecutionException {
        PropertyContainer propertyContainer = new FilePropertyContainer(getPropertyFileURL(propertyFileArtifact,
                propertyFile));

        CheckPropertyDuplicateInPropertyFile duplicateChecker = new CheckPropertyDuplicateInPropertyFile(
                propertyContainer);
        CheckPropertyExists propertyExistsChecker = new CheckPropertyExists(propertyContainer);

        List<Defect> defects = new ArrayList<Defect>();
        defects.addAll(duplicateChecker.checkForErrors());

        for (Pacify pfListEntity : pfEntityManager.getPacifyFiles()) {
            defects.addAll(propertyExistsChecker.checkForErrors(pfListEntity));
        }

        return defects;
    }
}
