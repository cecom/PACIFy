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

import java.util.Properties;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;

public class GenerateFinalPropertyFileMojoTest extends AbstractMojoTestCase {

    Properties propertiesShouldLookLike = new Properties();

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        propertiesShouldLookLike.put("env.name", "ChildOfChildEnv");
        propertiesShouldLookLike.put("SomeBaseProperty", "SomeBasePropertyValue");
        propertiesShouldLookLike.put("SomeChild1Property", "SomeChild1PropertyValue");
        propertiesShouldLookLike.put("SomeChild2Property", "SomeChild2PropertyValue");
        propertiesShouldLookLike.put("SomeChildOfChildProperty", "SomeChildOfChildPropertyValue");
    }

    public void testPseudo() {

    }

    public void doesNotWorkGeneratePropertyFile() throws Exception {
        GenerateFinalPropertyFile mavenMojo = (GenerateFinalPropertyFile) lookupMojo("generateFinalPropertyFile",
                "target/test-classes/GenerateFinalPropertyFile.pom");

        assertNotNull(mavenMojo);

        try {
            mavenMojo.execute();
            // MavenProject project = (MavenProject) getVariableValueFromObject(mavenMojo, "project");
            // assertEquals(propertiesShouldLookLike, project.getProperties());
        } catch (MojoExecutionException e) {
            fail(e.getMessage());
        }
    }
}
