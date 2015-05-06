package com.geewhiz.pacify;

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

import static org.testng.Assert.assertEquals;

import java.net.URL;
import java.util.Properties;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.geewhiz.pacify.property.resolver.fileresolver.FilePropertyResolver;

public class TestRecursivePropertyFileLoader {

	Properties allPropertiesShouldLookLike = new Properties();
	Properties child1PropertiesShouldLookLike = new Properties();
	Properties child2PropertiesShouldLookLike = new Properties();
	Properties childOfChildPropertiesShouldLookLike = new Properties();
	Properties basePropertiesShouldLookLike = new Properties();

	@BeforeTest
	public void setUp() throws Exception {
		basePropertiesShouldLookLike.put("env.name", "baseEnvName");
		basePropertiesShouldLookLike.put("SomeBaseProperty", "SomeBasePropertyValue");

		child1PropertiesShouldLookLike.put("env.name", "child1EnvName");
		child1PropertiesShouldLookLike.put("SomeChild1Property", "SomeChild1PropertyValue");

		child2PropertiesShouldLookLike.put("env.name", "child2EnvName");
		child2PropertiesShouldLookLike.put("SomeChild2Property", "SomeChild2PropertyValue");

		childOfChildPropertiesShouldLookLike.put("env.name", "ChildOfChildEnv");
		childOfChildPropertiesShouldLookLike.put("SomeChildOfChildProperty", "SomeChildOfChildPropertyValue");

		allPropertiesShouldLookLike.putAll(basePropertiesShouldLookLike);
		allPropertiesShouldLookLike.putAll(child1PropertiesShouldLookLike);
		allPropertiesShouldLookLike.putAll(child2PropertiesShouldLookLike);
		allPropertiesShouldLookLike.putAll(childOfChildPropertiesShouldLookLike);
	}

	@Test
	public void testWithClasspath() {
		URL url = this.getClass().getClassLoader().getResource("properties/subfolder/ChildOfChilds.properties");

		FilePropertyResolver filePropertyResolver = new FilePropertyResolver(url);

		assertEquals(filePropertyResolver.getFileProperties(), allPropertiesShouldLookLike);
		assertEquals(filePropertyResolver.getLocalProperties(), childOfChildPropertiesShouldLookLike);
		assertEquals(filePropertyResolver.getParentPropertyFileProperties().get(0).getLocalProperties(),
		        child1PropertiesShouldLookLike);
		assertEquals(filePropertyResolver.getParentPropertyFileProperties().get(1)
		        .getLocalProperties(), child2PropertiesShouldLookLike);
		assertEquals(filePropertyResolver.getParentPropertyFileProperties().get(0)
		        .getParentPropertyFileProperties().get(0).getLocalProperties(), basePropertiesShouldLookLike);
	}
}
