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

import java.net.URL;
import java.util.Properties;

import org.apache.maven.plugin.MojoExecutionException;

import com.geewhiz.pacify.property.FilePropertyContainer;
import com.geewhiz.pacify.property.PropertyContainer;

/**
 * User: sop
 * Date: 10.05.11
 * Time: 10:08
 * 
 * @goal loadPropertyFileIntoMaven
 * @phase validate
 */
public class LoadPropertyFileIntoMavenMojo extends BaseMojo {

    /**
     * In which jar is the propertyFile contained?
     * 
     * @parameter
     * @required
     */
    protected String propertyFileArtifact;

    /**
     * Which property file should be used?
     * 
     * @parameter expression="${pflist.usePropertyFile}"
     */
    protected String propertyFile;

    @Override
    protected void executePFList() throws MojoExecutionException {
        URL propertyFileURL = getPropertyFileURL(propertyFileArtifact, propertyFile);

        PropertyContainer propertyContainer = new FilePropertyContainer(propertyFileURL);

        getLog().info("Loading properties from [" + propertyContainer.getPropertyLoadedFrom() + "]...");

        Properties properties = propertyContainer.getProperties();
        project.getProperties().putAll(properties);
    }

}