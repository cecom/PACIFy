package com.geewhiz.pacify.commandline.commands;

import java.io.File;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.geewhiz.pacify.Replacer;

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
public class ReplacerCommand extends BasePropertyResolverCommand {

	@Parameter(names = { "-env", "--envName" }, description = "The name of the Environment which the package is configured for.", required = true)
	private String envName;

	@Parameter(names = { "-p", "--package" }, description = "The package path which you want to configure.", required = true)
	private File packagePath;

	@Parameter(names = { "-cc", "--createCopy" }, description = "Create a copy and configure the copy.", required = false, arity = 1)
	private Boolean createCopy = Boolean.TRUE;

	@Parameter(names = { "-cd", "--copyDestination" }, description = "Where to write the copy of the original package to. If not specified a folder with name of the package + _ + envName is created.", required = false)
	private File copyDestination;

	public void configureReplacer(Replacer replacer) {
		replacer.setEnvName(envName);
		replacer.setPackagePath(packagePath);
		replacer.setCreateCopy(createCopy);
		replacer.setCopyDestination(copyDestination);
		if (createCopy && copyDestination == null) {
			copyDestination = new File(packagePath.getParentFile(), packagePath.getName() + "_" + envName);
			replacer.setCopyDestination(copyDestination);
		}
	}
}
