package com.geewhiz.pacify.commandline;

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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.EnumMap;

import org.apache.tools.ant.types.LogLevel;

import com.geewhiz.pacify.configuration.CommandLineParameter;

public class CommandLineUtils {

	public static EnumMap<CommandLineParameter, Object> getCommandLinePropertiesForPropertyReplacer(String[] args) {
		EnumMap<CommandLineParameter, Object> commandlineProperties = new EnumMap<CommandLineParameter, Object>(
		        CommandLineParameter.class);
		for (String string : args) {
			String[] split = string.split("=");
			String key = split[0];
			String value = split.length > 1 ? split[1] : null;
			if (key.equals("--propertyFile") && value != null) {
				URL propertyFileUrl = getPropertyFileUrl(value);
				commandlineProperties.put(CommandLineParameter.PropertyFileURL, propertyFileUrl);
			}
			if (key.equals("--startPath") && value != null) {
				File file = new File(value);
				if (!file.exists()) {
					throw new IllegalArgumentException("StartPath [" + value + "] does not exist... Aborting!");
				}
				if (!file.isDirectory()) {
					throw new IllegalArgumentException("StartPath [" + value + "] is not a directory... Aborting!");
				}
				commandlineProperties.put(CommandLineParameter.StartPath, file);
			}
			if (key.equals("--logLevel") && value != null) {
				commandlineProperties.put(CommandLineParameter.LogLevel, value);
			}

			if (key.equals("--help")) {
				commandlineProperties.put(CommandLineParameter.Help, true);
			}
		}

		if (commandlineProperties.containsKey(CommandLineParameter.Help) || commandlineProperties.isEmpty()) {
			return commandlineProperties;
		}

		if (!commandlineProperties.containsKey(CommandLineParameter.PropertyFileURL)) {
			printPropertyReplacerHelp();
			throw new IllegalArgumentException("[--propertyFile] is missing as parameter... Aborting!");
		}

		if (!commandlineProperties.containsKey(CommandLineParameter.StartPath)) {
			File file = new File(".");
			commandlineProperties.put(CommandLineParameter.StartPath, file);
		}

		if (!commandlineProperties.containsKey(CommandLineParameter.LogLevel)) {
			commandlineProperties.put(CommandLineParameter.LogLevel, LogLevel.INFO);
		}

		return commandlineProperties;
	}

	public static EnumMap<CommandLineParameter, Object> getCommandLinePropertiesForCreateResultPropertyFile(
	        String[] args) {
		EnumMap<CommandLineParameter, Object> commandlineProperties = new EnumMap<CommandLineParameter, Object>(
		        CommandLineParameter.class);
		for (String string : args) {
			String[] split = string.split("=");
			String key = split[0];
			String value = split.length > 1 ? split[1] : null;
			if (key.equals("--propertyFile") && value != null) {
				URL propertyFileUrl = getPropertyFileUrl(value);
				commandlineProperties.put(CommandLineParameter.PropertyFileURL, propertyFileUrl);
			}
			if (key.equals("--targetFile") && value != null) {
				File file = new File(value);
				commandlineProperties.put(CommandLineParameter.OutputType, OutputType.File);
				commandlineProperties.put(CommandLineParameter.TargetFile, file);
			}
			if (key.equals("--logLevel") && value != null) {
				commandlineProperties.put(CommandLineParameter.LogLevel, value);
			}

			if (key.equals("--help")) {
				commandlineProperties.put(CommandLineParameter.Help, true);
			}
		}

		if (commandlineProperties.containsKey(CommandLineParameter.Help) || commandlineProperties.isEmpty()) {
			return commandlineProperties;
		}

		if (!commandlineProperties.containsKey(CommandLineParameter.PropertyFileURL)) {
			printCreateResultPropertyFileHelp();
			throw new IllegalArgumentException("[--propertyFile] is missing as parameter... Aborting!");
		}

		if (!commandlineProperties.containsKey(CommandLineParameter.TargetFile)) {
			commandlineProperties.put(CommandLineParameter.OutputType, OutputType.Stdout);
		}

		if (!commandlineProperties.containsKey(CommandLineParameter.LogLevel)) {
			commandlineProperties.put(CommandLineParameter.LogLevel, LogLevel.INFO);
		}

		return commandlineProperties;
	}

	public static URL getPropertyFileUrl(String value) {
		File file = new File(value);
		if (file.exists() && file.isFile()) {
			try {
				return file.toURI().toURL();
			} catch (MalformedURLException e) {
				throw new RuntimeException(e);
			}
		}

		URL url = CommandLineUtils.class.getClassLoader().getResource(value);
		if (url != null) {
			return url;
		}

		throw new RuntimeException("Couldn't find property File [" + value + "] in Classpath nor absolute... Aborting!");
	}

	public static void printPropertyReplacerHelp() {
		System.out.println("Parameters:");
		System.out
		        .println(" --propertyFile=<path>   -> Which property file should be used for filtering. Has to be within classpath or a absolute path.");
		System.out
		        .println(" [--startPath]=<path>    -> The folder where we are looking recursively for *-CMFile.pacify files. If not set, using current folder.");
		System.out.println(" [--logLevel]=<level>    -> The logLevel (DEBUG, INFO, ERROR). Default is INFO");
		System.out.println(" [--help]                -> This info.");
	}

	public static void printCreateResultPropertyFileHelp() {
		System.out.println("Parameters:");
		System.out.println(" --propertyFile=<path>   -> Which property file should be used.");
		System.out
		        .println(" --targetFile=<path>     -> If your property file contains imports, they will be resolved and the result will be written to this file.");
		System.out.println(" [--logLevel]=<level>    -> The logLevel (DEBUG, INFO, ERROR). Default is INFO");
		System.out.println(" [--help]                -> This info.");
	}

}
