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

import org.testng.annotations.Test;

import com.geewhiz.pacify.replacer.OutputType;

public class TestCreateResultPropertyFile {

	@Test
	public void checkForNotCorrect() {
		File startPath = new File("target/test-classes/TestCreateResultPropertyFile");

		File myTestProperty = new File(startPath, "subfolder/ChildOfChilds.properties");
		File targetFile = new File(startPath, "result.properties");

		assertTrue("StartPath [" + startPath.getPath() + "] doesn't exist!", startPath.exists());

		EnumMap<Resolver.Parameter, Object> commandlineProperties = new EnumMap<Resolver.Parameter, Object>(
		        Resolver.Parameter.class);
		commandlineProperties.put(Resolver.Parameter.PropertyFileURL, TestUtil.getURLForFile(myTestProperty));
		commandlineProperties.put(Resolver.Parameter.OutputType, OutputType.File);
		commandlineProperties.put(Resolver.Parameter.TargetFile, targetFile);

		Resolver createResultPropertyFile = new Resolver(commandlineProperties);
		createResultPropertyFile.create();

		TestUtil.checkIfResultIsAsExpected(startPath);
	}
}
