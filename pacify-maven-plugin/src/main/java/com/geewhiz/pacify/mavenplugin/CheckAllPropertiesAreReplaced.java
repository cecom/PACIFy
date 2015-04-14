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

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

import com.geewhiz.pacify.Replacer;
import com.geewhiz.pacify.defect.Defect;
import com.geewhiz.pacify.model.EntityManager;
import com.geewhiz.pacify.model.PFile;
import com.geewhiz.pacify.model.Pacify;

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

	/**
	 * @parameter expression="${logLevel}" default-value="ERROR"
	 * @required
	 */
	private String logLevel;

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

		EntityManager pfEntityManager = new EntityManager(pfListStartPath);
		if (pfEntityManager.getPFListCount() == 0) {
			getLog().info("No pflist files found. Nothing to check.");
			return;
		}
		getLog().info("Found [" + pfEntityManager.getPFListCount() + "] PFList Files...");

		getLog().info("Checking files...");
		List<Defect> defects = new ArrayList<Defect>();
		for (Pacify pfListEntity : pfEntityManager.getPacifyFiles()) {
			for (PFile pfile : pfListEntity.getPfFileEntities()) {
				java.io.File file = pfListEntity.getAbsoluteFileFor(pfile);
				defects.addAll(Replacer.checkFileForNotReplacedStuff(file));
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
