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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.geewhiz.pacify.checker.PacifyCheck;
import com.geewhiz.pacify.defect.Defect;
import com.geewhiz.pacify.model.EntityManager;
import com.geewhiz.pacify.model.PMarker;

public abstract class BaseCheck {

	protected List<Defect> getDefects(PacifyCheck checker, File testStartPath) {
		EntityManager entityManager = new EntityManager(testStartPath);

		List<Defect> defects = new ArrayList<Defect>();
		for (PMarker pfListEntity : entityManager.getPacifyFiles()) {
			defects.addAll(checker.checkForErrors(pfListEntity));
		}
		return defects;
	}

}
