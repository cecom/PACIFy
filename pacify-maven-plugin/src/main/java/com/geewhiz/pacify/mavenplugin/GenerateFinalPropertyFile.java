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
import java.util.Set;
import java.util.TreeSet;

import org.apache.maven.plugin.MojoExecutionException;

import com.geewhiz.pacify.WritePropertyFile;
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

	/**
	 * the encoding of the output.
	 * @parameter"
	 */
	private String encoding = "utf-8";

	@Override
	protected void executePacify() throws MojoExecutionException {
		for (String propertyFile : propertyFiles.split(",")) {
			FilePropertyResolver propertyResolver = new FilePropertyResolver(getPropertyFileURL(propertyFileArtifact,
			        propertyFile));

			Set<PropertyResolver> propertyResolverList = new TreeSet<PropertyResolver>();
			propertyResolverList.add(propertyResolver);

			PropertyResolveManager propertyResolveManager = new PropertyResolveManager(propertyResolverList);

			WritePropertyFile writePropertyFile = createResolver(propertyResolveManager, propertyFile);
			writePropertyFile.writeTo();
		}
	}

	private WritePropertyFile createResolver(PropertyResolveManager propertyResolveManager, String propertyFile) {
		if (outputDirectory != null && !outputDirectory.exists()) {
			outputDirectory.mkdirs();
		}

		WritePropertyFile writePropertyFile = new WritePropertyFile(propertyResolveManager);
		writePropertyFile.setTargetFile(new File(outputDirectory, propertyFile));
		writePropertyFile.setOutputEncoding(encoding);
		writePropertyFile.setOutputType(outputDirectory != null ? WritePropertyFile.OutputType.File : WritePropertyFile.OutputType.Stdout);

		return writePropertyFile;
	}
}