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
import java.util.Set;
import java.util.TreeSet;

import org.apache.maven.plugin.MojoExecutionException;

import com.geewhiz.pacify.checks.impl.CheckPropertyDuplicateInPropertyFile;
import com.geewhiz.pacify.checks.impl.CheckPropertyExists;
import com.geewhiz.pacify.defect.Defect;
import com.geewhiz.pacify.model.EntityManager;
import com.geewhiz.pacify.model.PMarker;
import com.geewhiz.pacify.property.PropertyResolveManager;
import com.geewhiz.pacify.property.resolver.fileresolver.FilePropertyResolver;
import com.geewhiz.pacify.resolver.PropertyResolver;

/**
 * @goal checkDeliveryProperties
 * @phase install
 */
public class CheckDeliveryPropertyFiles extends BaseMojo {

	/**
	 * @parameter default-value="${project.build.outputDirectory}"
	 * @required
	 */
	private File pacifyStartPath;

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
	protected void executePacify() throws MojoExecutionException {
		if (!pacifyStartPath.exists()) {
			File outputDirectory = new File(project.getModel().getBuild().getOutputDirectory());
			if (pacifyStartPath.equals(outputDirectory)) {
				getLog().debug("Directory [" + pacifyStartPath.getAbsolutePath() + "] does  not exists. Nothing to do.");
				return; // if it is a maven project which doesn't have a target folder, do nothing.
			}
			throw new MojoExecutionException("The folder [" + pacifyStartPath.getAbsolutePath() + "] does not exist.");
		}

		EntityManager entityManager = new EntityManager(pacifyStartPath);
		if (entityManager.getPMarkerCount() == 0) {
			getLog().info("No pacify files found. Nothing to check.");
			return;
		}

		getLog().info("Found [" + entityManager.getPMarkerCount() + "] pacify Files...");

		List<Defect> defects = new ArrayList<Defect>();
		for (String propertyFile : propertyFiles.split(",")) {
			getLog().info("Checking property file [" + propertyFile + "] ...");
			defects.addAll(checkPropertyFile(entityManager, propertyFile));
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

	private List<Defect> checkPropertyFile(EntityManager entityManager, String propertyFile)
	        throws MojoExecutionException {
		FilePropertyResolver propertyResolver = new FilePropertyResolver(getPropertyFileURL(propertyFileArtifact,
		        propertyFile));

		Set<PropertyResolver> propertyResolverList = new TreeSet<PropertyResolver>();
		propertyResolverList.add(propertyResolver);

		PropertyResolveManager propertyResolveManager = new PropertyResolveManager(propertyResolverList);

		CheckPropertyDuplicateInPropertyFile duplicateChecker = new CheckPropertyDuplicateInPropertyFile(
		        propertyResolveManager);
		CheckPropertyExists propertyExistsChecker = new CheckPropertyExists(propertyResolveManager);

		List<Defect> defects = new ArrayList<Defect>();
		defects.addAll(duplicateChecker.checkForErrors());

		for (PMarker pMarker : entityManager.getPMarkers()) {
			defects.addAll(propertyExistsChecker.checkForErrors(pMarker));
		}

		return defects;
	}
}
