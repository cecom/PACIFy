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
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;

import com.geewhiz.pacify.common.logger.Log;
import com.geewhiz.pacify.defect.Defect;
import com.geewhiz.pacify.defect.PropertyNotReplacedDefect;
import com.geewhiz.pacify.model.EntityManager;
import com.geewhiz.pacify.property.FilePropertyContainer;
import com.geewhiz.pacify.property.PropertyContainer;
import com.geewhiz.pacify.replacer.PropertyFileReplacer;
import com.geewhiz.pacify.utils.Utils;

public class Replacer {

	public enum Parameter {
		PropertyFileURL, PackagePath, TargetFile
	}

	private Map<Parameter, Object> propertyMap;
	private Logger logger = Log.getInstance();

	public Replacer(Map<Parameter, Object> propertyMap) {
		this.propertyMap = propertyMap;
		logger.info("== Executing PFListPropertyReplacer [Version=" + Utils.getJarVersion() + "]");
		logger.info("     [StartPath=" + getPackagePath().getAbsolutePath() + "]");
		logger.info("     [PropertyFileURL=" + getCommandLinePropertyFileURL().getPath() + "]");
	}

	public void replace() {
		EntityManager entityManager = new EntityManager(getPackagePath());

		logger.info("==== Found [" + entityManager.getPFListCount() + "] pacify Files...");

		logger.info("==== Checking pacify files...");
		List<Defect> defects = entityManager.checkCorrectnessOfPFListFiles(getPropertyFile());
		shouldWeAbortIt(defects);

		logger.info("==== Doing Replacement...");
		defects = entityManager.doReplacement(getPropertyFile());
		shouldWeAbortIt(defects);

		logger.info("== Successfully finished...");
	}

	private File getPackagePath() {
		return (File) propertyMap.get(Parameter.PackagePath);
	}

	private PropertyContainer getPropertyFile() {
		return new FilePropertyContainer(getCommandLinePropertyFileURL());
	}

	private URL getCommandLinePropertyFileURL() {
		return (URL) propertyMap.get(Parameter.PropertyFileURL);
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

	public static List<Defect> checkFileForNotReplacedStuff(File file) {
		List<Defect> defects = new ArrayList<Defect>();

		String fileContent = com.geewhiz.pacify.utils.FileUtils.getFileInOneString(file);

		Pattern pattern = PropertyFileReplacer.getPattern("([^}]*)", false);
		Matcher matcher = pattern.matcher(fileContent);

		while (matcher.find()) {
			String propertyId = matcher.group(1);
			Defect defect = new PropertyNotReplacedDefect(file, propertyId);
			defects.add(defect);
		}
		return defects;
	}
}
