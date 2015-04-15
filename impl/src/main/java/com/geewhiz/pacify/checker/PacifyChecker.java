package com.geewhiz.pacify.checker;

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

import java.util.ArrayList;
import java.util.List;

import com.geewhiz.pacify.checker.checks.CheckPropertyDuplicateDefinedInPFList;
import com.geewhiz.pacify.checker.checks.CheckPropertyDuplicateInPropertyFile;
import com.geewhiz.pacify.checker.checks.CheckPropertyExists;
import com.geewhiz.pacify.checker.checks.CheckPropertyExistsInTargetFile;
import com.geewhiz.pacify.checker.checks.CheckTargetFileExist;
import com.geewhiz.pacify.defect.Defect;
import com.geewhiz.pacify.model.PMarker;
import com.geewhiz.pacify.property.PropertyContainer;

public class PacifyChecker {

	List<Check> checks = new ArrayList<Check>();
	List<PMarkerCheck> pMarkerChecks = new ArrayList<PMarkerCheck>();

	public PacifyChecker(PropertyContainer propertyContainer) {
		checks.add(new CheckPropertyDuplicateInPropertyFile(propertyContainer));
		pMarkerChecks.add(new CheckTargetFileExist());
		pMarkerChecks.add(new CheckPropertyDuplicateDefinedInPFList());
		pMarkerChecks.add(new CheckPropertyExists(propertyContainer));
		pMarkerChecks.add(new CheckPropertyExistsInTargetFile());
	}

	public List<Defect> check(PMarker pMarker) {
		List<Defect> defects = new ArrayList<Defect>();
		for (Check check : checks) {
			defects.addAll(check.checkForErrors());
		}
		for (PMarkerCheck pfListCheck : pMarkerChecks) {
			defects.addAll(pfListCheck.checkForErrors(pMarker));
		}
		return defects;
	}
}
