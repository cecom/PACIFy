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

import com.geewhiz.pacify.defect.Defect;
import com.geewhiz.pacify.defect.DefectUtils;
import com.geewhiz.pacify.model.EntityManager;
import com.geewhiz.pacify.model.PMarker;
import com.geewhiz.pacify.property.PropertyResolveManager;
import com.geewhiz.pacify.replacer.PropertyMarkerFileReplacer;
import com.geewhiz.pacify.utils.Utils;
import com.google.inject.Inject;
import com.marzapower.loggable.Log;
import com.marzapower.loggable.Loggable;

@Loggable(loggerName = "com.geewhiz.pacify")
public class Replacer {

	private PropertyResolveManager propertyResolveManager;
	private String envName;
	private File packagePath;
	private Boolean createCopy;
	private File copyDestination;

	@Inject
	public Replacer(PropertyResolveManager propertyResolveManager) {
		this.propertyResolveManager = propertyResolveManager;
	}

	public void execute() {
		Log.get().info("== Executing Replacer [Version=" + Utils.getJarVersion() + "]");
		Log.get().info("   [PackagePath=" + getPackagePath().getAbsolutePath() + "]");
		Log.get().info("   [EnvName=" + getEnvName() + "]");
		Log.get().info("   [CreateCopy=" + isCreateCopy() + "]");
		if (isCreateCopy()) {
			Log.get().info("   [Destination=" + getCopyDestination().getAbsolutePath() + "]");
		}

		if (isCreateCopy()) {
			createCopy();
		}

		File pathToConfigure = isCreateCopy() ? getCopyDestination() : getPackagePath();

		EntityManager entityManager = new EntityManager(pathToConfigure);

		Log.get().info("== Found [" + entityManager.getPMarkerCount() + "] pacify marker files");
		for (PMarker pMarker : entityManager.getPMarkers()) {
			Log.get().info("   [" + pMarker.getFile().getAbsolutePath() + "]");
		}
		Log.get().info("== Validating...");

		List<Defect> defects = createValidator().validateInternal(entityManager);
		DefectUtils.abortIfDefectExists(defects);

		Log.get().info("== Replacing...");
		defects = doReplacement(entityManager);
		DefectUtils.abortIfDefectExists(defects);

		Log.get().info("== Successfully finished");
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
			Log.get().debug("   Processing Marker File [" + pMarker.getFile().getAbsolutePath() + "]");
			PropertyMarkerFileReplacer propertyReplacer = new PropertyMarkerFileReplacer(propertyResolveManager,
			        pMarker);
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
