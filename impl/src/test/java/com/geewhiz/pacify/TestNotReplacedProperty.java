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
import java.net.URL;
import java.util.EnumMap;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.geewhiz.pacify.TODO.EntityManager;
import com.geewhiz.pacify.commandline.CommandLineParameter;
import com.geewhiz.pacify.defect.Defect;
import com.geewhiz.pacify.property.FilePropertyContainer;
import com.geewhiz.pacify.utils.FileUtils;

public class TestNotReplacedProperty extends BaseCheck {

	@Test
	public void checkForNotCorrect() {
		File startPath = new File("target/test-classes/notReplacedPropertyTest");
		File myTestProperty = new File(startPath, "myProperties.properties");
		URL myTestPropertyURL = FileUtils.getFileUrl(myTestProperty);

		assertTrue("StartPath [" + startPath.getPath() + "] doesn't exist!", startPath.exists());

		EnumMap<CommandLineParameter, Object> commandlineProperties = new EnumMap<CommandLineParameter, Object>(
		        CommandLineParameter.class);
		commandlineProperties.put(CommandLineParameter.StartPath, startPath);
		commandlineProperties.put(CommandLineParameter.PropertyFileURL, Util.getURLForFile(myTestProperty));
		commandlineProperties.put(CommandLineParameter.LogLevel, "debug");

		EntityManager pfEntityManager = new EntityManager(startPath);
		List<Defect> defects = pfEntityManager.doReplacement(new FilePropertyContainer(myTestPropertyURL));

		Assert.assertEquals(defects.size(), 4);
	}
}
