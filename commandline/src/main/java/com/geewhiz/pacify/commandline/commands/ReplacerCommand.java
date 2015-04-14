package com.geewhiz.pacify.commandline.commands;

import java.io.File;
import java.util.EnumMap;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.geewhiz.pacify.Replacer;
import com.geewhiz.pacify.common.file.FileUtils;

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

@Parameters(separators = "=", commandDescription = "Used to configure a package.")
public class ReplacerCommand {
	@Parameter(names = { "-p", "--package" }, description = "The package path which you want to configure.", required = true)
	private File packagePath;

	@Parameter(names = { "-pf", "--propertyFile" }, description = "The property file where we get the property values from", required = true)
	private String propertyFile;

	public File getPackagePath() {
		return packagePath;
	}

	public String getPropertyFile() {
		return propertyFile;
	}

	public EnumMap<Replacer.Parameter, Object> getPropertyMap() {
		EnumMap<Replacer.Parameter, Object> result = new EnumMap<Replacer.Parameter, Object>(Replacer.Parameter.class);
		result.put(Replacer.Parameter.PackagePath, getPackagePath());
		result.put(Replacer.Parameter.PropertyFileURL, FileUtils.getFileUrl(propertyFile));
		return result;
	}
}
