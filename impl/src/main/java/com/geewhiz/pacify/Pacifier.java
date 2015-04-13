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
import java.util.EnumMap;
import java.util.List;

import org.apache.tools.ant.types.LogLevel;
import org.slf4j.Logger;

import com.geewhiz.pacify.TODO.EntityManager;
import com.geewhiz.pacify.commandline.CommandLineParameter;
import com.geewhiz.pacify.commandline.CommandLineUtils;
import com.geewhiz.pacify.defect.Defect;
import com.geewhiz.pacify.logger.Log;
import com.geewhiz.pacify.property.FilePropertyContainer;
import com.geewhiz.pacify.property.PropertyContainer;
import com.geewhiz.pacify.utils.Utils;

public class Pacifier {

	EnumMap<CommandLineParameter, Object> commandlineProperties;
	Logger logger = Log.getInstance();

	/**
	 * @param args the PFProperty PFFile which will be used for replacement
	 *            --property_file=<path to property file>
	 *            [--path]=<path where to start the replacement, if not given the current testAll.folder is used>
	 *            [--logLevel]=<LogLevel>, defaults to LogLevel.Info
	 *            [--help] print help
	 * @see LogLevel
	 */
	public static void main(String[] args) {
		EnumMap<CommandLineParameter, Object> commandlineProperties = CommandLineUtils
		        .getCommandLinePropertiesForPropertyReplacer(args);

		if (commandlineProperties.containsKey(CommandLineParameter.Help) || commandlineProperties.isEmpty()) {
			CommandLineUtils.printPropertyReplacerHelp();
			return;
		}

		Pacifier pacifier = new Pacifier(commandlineProperties);
		pacifier.replace();
	}

	public Pacifier(EnumMap<CommandLineParameter, Object> commandlineProperties) {
		this.commandlineProperties = commandlineProperties;
		logger.info("== Executing PFListPropertyReplacer [Version=" + Utils.getJarVersion() + "]");
		logger.info("     [StartPath=" + getCommandLineStartPath().getAbsolutePath() + "]");
		logger.info("     [PropertyFileURL=" + getCommandLinePropertyFileURL().getPath() + "]");
	}

	public void replace() {
		EntityManager entityManager = new EntityManager(getCommandLineStartPath());

		logger.info("==== Found [" + entityManager.getPFListCount() + "] PFList Files...");

		logger.info("==== Checking PFListFiles...");
		List<Defect> defects = entityManager.checkCorrectnessOfPFListFiles(getPropertyFile());
		shouldWeAbortIt(defects);

		logger.info("==== Doing Replacement...");
		defects = entityManager.doReplacement(getPropertyFile());
		shouldWeAbortIt(defects);

		logger.info("== Successfully finished...");
	}

	private File getCommandLineStartPath() {
		return (File) commandlineProperties.get(CommandLineParameter.StartPath);
	}

	private PropertyContainer getPropertyFile() {
		return new FilePropertyContainer(getCommandLinePropertyFileURL());
	}

	private URL getCommandLinePropertyFileURL() {
		return (URL) commandlineProperties.get(CommandLineParameter.PropertyFileURL);
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
