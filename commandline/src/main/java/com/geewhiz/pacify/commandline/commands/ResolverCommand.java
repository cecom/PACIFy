package com.geewhiz.pacify.commandline.commands;

import java.io.File;
import java.util.EnumMap;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.geewhiz.pacify.Resolver;
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

@Parameters(separators = "=", commandDescription = "Used to resolve the property file with its dependencies")
public class ResolverCommand {

	@Parameter(names = { "-pf", "--propertyFile" }, description = "The property file where we get the property values from.", required = true)
	private String propertyFile;

	@Parameter(names = { "-tf", "--targetFile" }, description = "Where to write the result to. If not given, it will be printed to stdout", required = false)
	private File targetFile;

	public String getPropertyFile() {
		return propertyFile;
	}

	public File getTargetFile() {
		return targetFile;
	}

	public EnumMap<Resolver.Parameter, Object> getPropertyMap() {
		EnumMap<Resolver.Parameter, Object> result = new EnumMap<Resolver.Parameter, Object>(Resolver.Parameter.class);
		result.put(Resolver.Parameter.PropertyFileURL, FileUtils.getFileUrl(getPropertyFile()));
		if (getTargetFile() != null) {
			result.put(Resolver.Parameter.OutputType, Resolver.OutputType.File);
			result.put(Resolver.Parameter.TargetFile, getTargetFile());
		} else {
			result.put(Resolver.Parameter.OutputType, Resolver.OutputType.Stdout);
		}
		return result;
	}
}
