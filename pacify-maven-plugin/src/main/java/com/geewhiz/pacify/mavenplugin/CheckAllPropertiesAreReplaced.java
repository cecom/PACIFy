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

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

import com.geewhiz.pacify.checker.checks.CheckForNotReplacedTokens;
import com.geewhiz.pacify.defect.Defect;
import com.geewhiz.pacify.model.EntityManager;
import com.geewhiz.pacify.model.PFile;
import com.geewhiz.pacify.model.PMarker;

/**
 * 
 * @goal checkAllPropertiesAreReplaced
 * @phase install
 */
public class CheckAllPropertiesAreReplaced extends AbstractMojo {

	/**
	 * @parameter default-value="${project}"
	 * @required
	 * @readonly
	 */
	protected MavenProject project;

	/**
	 * @parameter default-value="${project.build.outputDirectory}"
	 * @required
	 */
	private java.io.File pfListStartPath;

	/**
	 * Should it be skipped??
	 * 
	 * @parameter expression="${skipPFList}" default-value="false"
	 */
	protected boolean skip;

	public void execute() throws MojoExecutionException {
		if (skip) {
			getLog().info("PFList is skipped.");
			return;
		}

		if (!pfListStartPath.exists()) {
			java.io.File outputDirectory = new java.io.File(project.getModel().getBuild().getOutputDirectory());
			if (pfListStartPath.equals(outputDirectory)) {
				getLog().debug("Directory [" + pfListStartPath.getAbsolutePath() + "] does  not exists. Nothing to do.");
				return; // if it is a maven project which doesn't have a target folder, do nothing.
			}
			throw new MojoExecutionException("The folder [" + pfListStartPath.getAbsolutePath() + "] does not exist.");
		}

		EntityManager entityManager = new EntityManager(pfListStartPath);
		if (entityManager.getPMarkerCount() == 0) {
			getLog().info("No pflist files found. Nothing to check.");
			return;
		}
		getLog().info("Found [" + entityManager.getPMarkerCount() + "] pacify files...");

		getLog().info("Checking files...");
		CheckForNotReplacedTokens checker = new CheckForNotReplacedTokens();
		List<Defect> defects = new ArrayList<Defect>();
		for (PMarker pMarker : entityManager.getPMarkers()) {
			for (PFile pfile : pMarker.getPFiles()) {
				File file = pMarker.getAbsoluteFileFor(pfile);
				defects.addAll(checker.checkForErrors(file));
			}
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
}
