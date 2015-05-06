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
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;

import com.geewhiz.pacify.common.logger.Log;
import com.geewhiz.pacify.defect.Defect;
import com.geewhiz.pacify.defect.DefectUtils;
import com.geewhiz.pacify.model.EntityManager;
import com.geewhiz.pacify.model.PMarker;
import com.geewhiz.pacify.property.PropertyResolveManager;
import com.geewhiz.pacify.replacer.PropertyMarkerFileReplacer;
import com.geewhiz.pacify.utils.Utils;
import com.google.inject.Inject;

public class Replacer {

	private PropertyResolveManager propertyResolveManager;
	private Logger logger = Log.getInstance();
	private String envName;
	private File packagePath;
	private Boolean createCopy;
	private File copyDestination;

	@Inject
	public Replacer(PropertyResolveManager propertyResolveManager) {
		this.propertyResolveManager = propertyResolveManager;
	}

	public void execute() {
		logger.info("== Executing Replacer [Version=" + Utils.getJarVersion() + "]");
		logger.info("     [PackagePath=" + getPackagePath().getAbsolutePath() + "]");
		logger.info("     [EnvName=" + getEnvName() + "]");
		logger.info("     [CreateCopy=" + isCreateCopy() + "]");
		if (isCreateCopy()) {
			logger.info("     [Destination=" + getCopyDestination().getAbsolutePath() + "]");
		}

		if (isCreateCopy()) {
			createCopy();
		}

		File pathToConfigure = isCreateCopy() ? getCopyDestination() : getPackagePath();

		EntityManager entityManager = new EntityManager(pathToConfigure);

		logger.info("==== Found [" + entityManager.getPMarkerCount() + "] pacify files...");
		logger.info("==== Validating ...");

		List<Defect> defects = createValidator().validateInternal(entityManager);
		DefectUtils.abortIfDefectExists(defects);

		logger.info("==== Doing Replacement...");
		defects = doReplacement(entityManager);
		DefectUtils.abortIfDefectExists(defects);

		logger.info("== Successfully finished...");
	}

	public String getEnvName() {
		return envName;
	}

	public void setEnvName(String envName) {
		this.envName = envName;
	}

	public File getPackagePath() {
		return packagePath;
	}

	public void setPackagePath(File packagePath) {
		this.packagePath = packagePath;
	}

	public Boolean isCreateCopy() {
		return createCopy;
	}

	public void setCreateCopy(Boolean createCopy) {
		this.createCopy = createCopy;
	}

	public File getCopyDestination() {
		return copyDestination;
	}

	public void setCopyDestination(File copyDestination) {
		this.copyDestination = copyDestination;
	}

	private void createCopy() {
		try {
			FileUtils.copyDirectory(getPackagePath(), getCopyDestination());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public List<Defect> doReplacement(EntityManager entityManager) {
		List<Defect> defects = new ArrayList<Defect>();
		for (PMarker pMarker : entityManager.getPMarkers()) {
			logger.info("====== Replacing stuff which is configured in [" + pMarker.getFile().getPath()
			        + "] ...");
			PropertyMarkerFileReplacer propertyReplacer = new PropertyMarkerFileReplacer(propertyResolveManager, pMarker);
			defects.addAll(propertyReplacer.replace());
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
