package com.geewhiz.pacify.checks.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.geewhiz.pacify.defect.Defect;
import com.geewhiz.pacify.defect.PropertyNotReplacedDefect;
import com.geewhiz.pacify.replacer.PropertyMarkerFileReplacer;

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

public class CheckForNotReplacedTokens {

	public List<Defect> checkForErrors(File file) {
		List<Defect> defects = new ArrayList<Defect>();

		String fileContent = com.geewhiz.pacify.utils.FileUtils.getFileInOneString(file);

		// todo: das pattern muss raus, kann file spezifisch sein
		Pattern pattern = PropertyMarkerFileReplacer.getPattern("([^}]*)", false);
		Matcher matcher = pattern.matcher(fileContent);

		while (matcher.find()) {
			String propertyId = matcher.group(1);
			Defect defect = new PropertyNotReplacedDefect(file, propertyId);
			defects.add(defect);
		}
		return defects;
	}

}
