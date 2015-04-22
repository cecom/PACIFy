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

import static org.testng.AssertJUnit.assertTrue;

import java.io.File;
import java.util.EnumMap;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import org.testng.annotations.Test;

import com.geewhiz.pacify.property.PropertyResolveManager;
import com.geewhiz.pacify.property.resolver.cmdresolver.CmdPropertyResolver;
import com.geewhiz.pacify.resolver.PropertyResolver;

public class TestCmdResolver {

	@Test
	public void testAll() {
		File startPath = new File("target/test-classes/TestCmdResolver");
		assertTrue("StartPath [" + startPath.getPath() + "] doesn't exist!", startPath.exists());

		PropertyResolveManager propertyResolveManager = getPropertyResolveManager(startPath);

		EnumMap<Replacer.Parameter, Object> commandlineProperties =
		        new EnumMap<Replacer.Parameter, Object>(Replacer.Parameter.class);
		commandlineProperties.put(Replacer.Parameter.PackagePath, startPath);

		Replacer replacer = new Replacer(propertyResolveManager);
		replacer.setParameters(commandlineProperties);
		replacer.replace();

		TestUtil.checkIfResultIsAsExpected(startPath);
	}

	private PropertyResolveManager getPropertyResolveManager(File startPath) {
		Properties properties = new Properties();
		properties.put("foobar3", "%{foobar1}:%{foobar2}");
		properties.put("foobar2", "6299äÖ9");
		properties.put("foobar5", "%{foobar6}");
		properties.put("foobar6", "%{foobar7}");
		properties.put("foobar7", "someProperty");
		properties.put("path", "d:\\tmp\\somefolder");
		properties.put("foobar1", "http://0815");
		properties.put("foobar4", "%{foobar2}/%{foobar1}/%{foobar5}");

		CmdPropertyResolver cmdPropertyResolver = new CmdPropertyResolver(properties);

		Set<PropertyResolver> resolverList = new TreeSet<PropertyResolver>();
		resolverList.add(cmdPropertyResolver);

		PropertyResolveManager propertyResolveManager = new PropertyResolveManager(resolverList);
		return propertyResolveManager;
	}
}
