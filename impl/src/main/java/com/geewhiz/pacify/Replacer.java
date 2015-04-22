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
import java.util.EnumMap;
import java.util.List;

import org.slf4j.Logger;

import com.geewhiz.pacify.common.logger.Log;
import com.geewhiz.pacify.defect.Defect;
import com.geewhiz.pacify.model.EntityManager;
import com.geewhiz.pacify.property.PropertyResolveManager;
import com.geewhiz.pacify.utils.Utils;
import com.google.inject.Inject;

public class Replacer {

	public enum Parameter {
		PackagePath, TargetFile
	}

	private PropertyResolveManager propertyResolveManager;
	private EnumMap<Parameter, Object> commandLineParamerters;
	private Logger logger = Log.getInstance();

	@Inject
	public Replacer(PropertyResolveManager propertyResolveManager) {
		this.propertyResolveManager = propertyResolveManager;
	}

	public void setCommandLineParameters(EnumMap<Parameter, Object> commandLineParamerters) {
		this.commandLineParamerters = commandLineParamerters;
		logger.info("== Executing PFListPropertyReplacer [Version=" + Utils.getJarVersion() + "]");
		logger.info("     [StartPath=" + getPackagePath().getAbsolutePath() + "]");
	}

	public void replace() {
		EntityManager entityManager = new EntityManager(getPackagePath());

		logger.info("==== Found [" + entityManager.getPMarkerCount() + "] pacify Files...");

		logger.info("==== Checking pacify files...");
		List<Defect> defects = entityManager.validate(propertyResolveManager);
		shouldWeAbortIt(defects);

		logger.info("==== Doing Replacement...");
		defects = entityManager.doReplacement(propertyResolveManager);
		shouldWeAbortIt(defects);

		logger.info("== Successfully finished...");
	}

	private File getPackagePath() {
		return (File) commandLineParamerters.get(Parameter.PackagePath);
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
