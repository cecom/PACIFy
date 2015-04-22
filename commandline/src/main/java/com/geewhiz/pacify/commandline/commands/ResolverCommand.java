package com.geewhiz.pacify.commandline.commands;

import java.io.File;
import java.util.EnumMap;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.geewhiz.pacify.Resolver;

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
public class ResolverCommand extends BasePropertyResolverCommand {

	@Parameter(names = { "-d", "--destinationFile" }, description = "Where to write the result to. If not given, it will be printed to stdout", required = false)
	private File targetFile;

	@Parameter(names = { "-e", "--targetEncoding" }, description = "Which encoding do you want in the created file", required = false)
	private String targetEncoding = "utf-8";

	public EnumMap<Resolver.Parameter, Object> getCommandlineParameters() {
		EnumMap<Resolver.Parameter, Object> result = new EnumMap<Resolver.Parameter, Object>(Resolver.Parameter.class);
		if (targetFile != null) {
			result.put(Resolver.Parameter.OutputType, Resolver.OutputType.File);
			result.put(Resolver.Parameter.TargetFile, targetFile);
		} else {
			result.put(Resolver.Parameter.OutputType, Resolver.OutputType.Stdout);
		}
		result.put(Resolver.Parameter.OutputEncodingType, targetEncoding);
		return result;
	}
}
