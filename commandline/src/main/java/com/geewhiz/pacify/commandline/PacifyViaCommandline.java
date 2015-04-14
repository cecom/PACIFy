package com.geewhiz.pacify.commandline;

import org.slf4j.Logger;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.geewhiz.pacify.Replacer;
import com.geewhiz.pacify.Resolver;
import com.geewhiz.pacify.commandline.commands.MainCommand;
import com.geewhiz.pacify.commandline.commands.ReplacerCommand;
import com.geewhiz.pacify.commandline.commands.ResolverCommand;
import com.geewhiz.pacify.common.logger.Log;

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

public class PacifyViaCommandline {

	private static Logger logger = Log.getInstance();

	public static void main(String... args) {
		int resultValue = mainInternal(args);
		logger.debug("Exiting with exit code {}", resultValue);
		System.exit(resultValue);
	}

	protected static int mainInternal(String[] args) {
		MainCommand mainCommand = new MainCommand();
		ReplacerCommand replacerCommand = new ReplacerCommand();
		ResolverCommand resolverCommand = new ResolverCommand();

		JCommander jc = new JCommander(mainCommand);
		jc.addCommand("replace", replacerCommand);
		jc.addCommand("resolve", resolverCommand);

		try {
			jc.parse(args);
		} catch (ParameterException e) {
			System.err.println(e.getMessage());
			return 1;
		}

		if ("replace".equals(jc.getParsedCommand())) {
			return executeReplacer(replacerCommand);
		} else if ("resolve".equals(jc.getParsedCommand())) {
			return executeResolver(resolverCommand);
		} else {
			jc.usage();
		}
		return 1;
	}

	private static int executeResolver(ResolverCommand resolverCommand) {
		Resolver resolver = new Resolver(resolverCommand.getPropertyMap());
		resolver.create();
		return 0;
	}

	private static int executeReplacer(ReplacerCommand replacerCommand) {
		Replacer replacer = new Replacer(replacerCommand.getPropertyMap());
		replacer.replace();
		return 0;
	}
}
