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
import java.util.EnumMap;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;

import com.geewhiz.pacify.common.logger.Log;
import com.geewhiz.pacify.defect.Defect;
import com.geewhiz.pacify.model.EntityManager;
import com.geewhiz.pacify.property.PropertyResolveManager;
import com.geewhiz.pacify.utils.Utils;
import com.google.inject.Inject;

public class Replacer {

	public enum Parameter {
		EnvName, PackagePath, CreateCopy, CopyDestination
	}

	private PropertyResolveManager propertyResolveManager;
	private EnumMap<Parameter, Object> parameters;
	private Logger logger = Log.getInstance();

	@Inject
	public Replacer(PropertyResolveManager propertyResolveManager) {
		this.propertyResolveManager = propertyResolveManager;
	}

	public void setParameters(EnumMap<Parameter, Object> parameters) {
		this.parameters = parameters;
		logger.info("== Executing PFListPropertyReplacer [Version=" + Utils.getJarVersion() + "]");
		logger.info("     [PackagePath=" + getPackagePath().getAbsolutePath() + "]");
		logger.info("     [EnvName=" + getEnvName() + "]");
		logger.info("     [CreateCopy=" + isCreateCopy() + "]");
		if (isCreateCopy()) {
			logger.info("     [Destination=" + getCopyDestination().getAbsolutePath() + "]");
		}
	}

	public void replace() {
		if (isCreateCopy()) {
			createCopy();
		}

		File pathToConfigure = isCreateCopy() ? getCopyDestination() : getPackagePath();

		EntityManager entityManager = new EntityManager(pathToConfigure);

		logger.info("==== Found [" + entityManager.getPMarkerCount() + "] pacify Files...");

		logger.info("==== Checking pacify files...");
		List<Defect> defects = entityManager.validate(propertyResolveManager);
		shouldWeAbortIt(defects);

		logger.info("==== Doing Replacement...");
		defects = entityManager.doReplacement(propertyResolveManager);
		shouldWeAbortIt(defects);

		logger.info("== Successfully finished...");
	}

	private String getEnvName() {
		return (String) parameters.get(Parameter.EnvName);
	}

	public File getPackagePath() {
		return (File) parameters.get(Parameter.PackagePath);
	}

	public Boolean isCreateCopy() {
		if (parameters.get(Parameter.CreateCopy) == null) {
			throw new IllegalArgumentException("Parameter.CreateCopy not defined.");
		}
		return (Boolean) parameters.get(Parameter.CreateCopy);
	}

	public File getCopyDestination() {
		if (parameters.get(Parameter.CopyDestination) == null) {
			throw new IllegalArgumentException("No copy destination defined.");
		}
		return (File) parameters.get(Parameter.CopyDestination);
	}

	private void createCopy() {
		try {
			FileUtils.copyDirectory(getPackagePath(), getCopyDestination());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void shouldWeAbortIt(List<Defect> defects) {
		if (defects.isEmpty()) {
			return;
		}

		logger.error("==== !!!!!! We got Errors !!!!! ...");
		for (Defect defect : defects) {
			logger.error(defect.getDefectMessage());
		}
		throw new RuntimeException("We got errors... Aborting!");
	}
}
