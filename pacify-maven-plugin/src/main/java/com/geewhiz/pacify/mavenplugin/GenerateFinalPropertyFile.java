package com.geewhiz.pacify.mavenplugin;

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
import java.util.Set;
import java.util.TreeSet;

import org.apache.maven.plugin.MojoExecutionException;

import com.geewhiz.pacify.Resolver;
import com.geewhiz.pacify.property.PropertyResolveManager;
import com.geewhiz.pacify.property.resolver.fileresolver.FilePropertyResolver;
import com.geewhiz.pacify.resolver.PropertyResolver;

/**
 * @goal generateFinalPropertyFile
 * @phase process-resources
 */
public class GenerateFinalPropertyFile extends BaseMojo {

	/**
	 * if given, the property files will be written to this directory. if not given it will be written to stdout
	 * @parameter"
	 */
	private File outputDirectory;

	/**
	 * which files should be generated? its a comma separated list
	 * 
	 * @parameter
	 * @required
	 */
	private String propertyFiles;

	/**
	 * In which jar is the propertyFile contained?
	 * 
	 * @parameter
	 * @required
	 */
	protected String propertyFileArtifact;

	@Override
	protected void executePFList() throws MojoExecutionException {
		for (String propertyFile : propertyFiles.split(",")) {
			EnumMap<Resolver.Parameter, Object> commandlineProperties = new EnumMap<Resolver.Parameter, Object>(
			        Resolver.Parameter.class);
			commandlineProperties.put(Resolver.Parameter.PropertyFileURL,
			        getPropertyFileURL(propertyFileArtifact, propertyFile));

			if (outputDirectory != null) {
				if (!outputDirectory.exists()) {
					outputDirectory.mkdirs();
				}
				File targetFile = new File(outputDirectory, propertyFile);
				getLog().info("Creating final property file [" + targetFile.getPath() + "] ...");
				commandlineProperties.put(Resolver.Parameter.OutputType, Resolver.OutputType.File);
				commandlineProperties.put(Resolver.Parameter.TargetFile, targetFile);
			} else {
				getLog().info("Creating final property file [" + propertyFile + "] ...");
				commandlineProperties.put(Resolver.Parameter.OutputType, Resolver.OutputType.Stdout);
			}

			FilePropertyResolver propertyResolver = new FilePropertyResolver(getPropertyFileURL(propertyFileArtifact,
			        propertyFile));

			Set<PropertyResolver> propertyResolverList = new TreeSet<PropertyResolver>();
			propertyResolverList.add(propertyResolver);

			PropertyResolveManager propertyResolveManager = new PropertyResolveManager(propertyResolverList);

			Resolver createResultPropertyFile = new Resolver(propertyResolveManager);
			createResultPropertyFile.setCommandLineParameters(commandlineProperties);
			createResultPropertyFile.create();
		}
	}
}