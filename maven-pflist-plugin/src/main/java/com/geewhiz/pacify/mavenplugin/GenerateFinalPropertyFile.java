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

import org.apache.maven.plugin.MojoExecutionException;

import com.geewhiz.pacify.CreateResultPropertyFile;
import com.geewhiz.pacify.commandline.CommandLineParameter;
import com.geewhiz.pacify.commandline.OutputType;

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
            EnumMap<CommandLineParameter, Object> commandlineProperties = new EnumMap<CommandLineParameter, Object>(
                    CommandLineParameter.class);
            commandlineProperties.put(CommandLineParameter.PropertyFileURL,
                    getPropertyFileURL(propertyFileArtifact, propertyFile));
            commandlineProperties.put(CommandLineParameter.LogLevel, getLogLevel());

            if (outputDirectory != null) {
                if (!outputDirectory.exists()) {
                    outputDirectory.mkdirs();
                }
                File targetFile = new File(outputDirectory, propertyFile);
                getLog().info("Creating final property file [" + targetFile.getPath() + "] ...");
                commandlineProperties.put(CommandLineParameter.OutputType, OutputType.File);
                commandlineProperties.put(CommandLineParameter.TargetFile, targetFile);
            } else {
                getLog().info("Creating final property file [" + propertyFile + "] ...");
                commandlineProperties.put(CommandLineParameter.OutputType, OutputType.Stdout);
            }

            CreateResultPropertyFile createResultPropertyFile = new CreateResultPropertyFile(commandlineProperties);
            createResultPropertyFile.create();
        }
    }
}