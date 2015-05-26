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
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.maven.plugin.MojoExecutionException;

import com.geewhiz.pacify.Replacer;
import com.geewhiz.pacify.Validator;
import com.geewhiz.pacify.defect.Defect;
import com.geewhiz.pacify.defect.DefectUtils;
import com.geewhiz.pacify.managers.EntityManager;
import com.geewhiz.pacify.managers.PropertyResolveManager;
import com.geewhiz.pacify.property.resolver.fileresolver.FilePropertyResolver;
import com.geewhiz.pacify.resolver.PropertyResolver;

/**
 * @goal replace
 * @phase generate-resources
 */
public class ReplaceMojo extends BaseMojo {

	/**
	 * @parameter default-value="${project.build.outputDirectory}"
	 * @required
	 */
	private File startPath;

	/**
	 * should we use the maven properties instead of a file?
	 * 
	 * @parameter default-value="true"
	 * @required
	 */
	protected boolean useMavenProperties;

	/**
	 * If you defined useMavenProperties with false, you have to define the propertyFile.
	 * 
	 * @parameter expression="${pacify.usePropertyFile}"
	 */
	protected String propertyFile;

	/**
	 * If you defined useMavenProperties with false, you have to define in which jar is the propertyFile contained?
	 * 
	 * @parameter
	 */
	protected String propertyFileArtifact;

	@Override
	protected void executePacify() throws MojoExecutionException {
		if (!startPath.exists()) {
			File outputDirectory = new File(project.getModel().getBuild().getOutputDirectory());
			if (startPath.equals(outputDirectory)) {
				getLog().debug("Directory [" + startPath.getAbsolutePath() + "] does  not exists. Nothing to do.");
				return; // if it is a maven project which doesn't have a target folder, do nothing.
			}
			throw new MojoExecutionException("The folder [" + startPath.getAbsolutePath() + "] does not exist.");
		}

		EntityManager entityManager = new EntityManager(startPath);
		if (entityManager.getPMarkerCount() == 0) {
			getLog().info("No pacify files found. Nothing to do.");
			return;
		}
		getLog().info("Found [" + entityManager.getPMarkerCount() + "] pacify Files...");

		PropertyResolver propertyResolver;
		if (useMavenProperties) {
			propertyResolver = new MavenPropertyContainer(project.getProperties(), project.getModel()
			        .getModelEncoding());
		} else {
			propertyResolver = new FilePropertyResolver(getPropertyFileURL(propertyFileArtifact,
			        propertyFile));
		}

		getLog().info("Loading properties from [" + propertyResolver.getPropertyResolverDescription() + "]... ");
		getLog().info("Checking pacify files...");

		Set<PropertyResolver> propertyResolverList = new TreeSet<PropertyResolver>();
		propertyResolverList.add(propertyResolver);

		PropertyResolveManager propertyResolveManager = new PropertyResolveManager(propertyResolverList);

		getLog().info("Validating ...");
		List<Defect> defects = createValidator(propertyResolveManager).validateInternal(entityManager);
		DefectUtils.abortIfDefectExists(defects);

		getLog().info("Doing Replacement...");
		defects = createReplacer(propertyResolveManager).doReplacement(entityManager);
		DefectUtils.abortIfDefectExists(defects);
	}

	private Replacer createReplacer(PropertyResolveManager propertyResolveManager) {
		Replacer replacer = new Replacer(propertyResolveManager);
		return replacer;
	}

	private Validator createValidator(PropertyResolveManager propertyResolveManager) {
		Validator validator = new Validator(propertyResolveManager);
		validator.setPackagePath(startPath);
		validator.enableMarkerFileChecks();
		validator.enablePropertyResolveChecks();
		return validator;
	}
}
