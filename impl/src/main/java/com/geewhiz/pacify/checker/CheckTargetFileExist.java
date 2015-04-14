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

import com.geewhiz.pacify.defect.Defect;
import com.geewhiz.pacify.defect.TargetFileDoesNotExistDefect;
import com.geewhiz.pacify.model.PFile;
import com.geewhiz.pacify.model.PProperty;
import com.geewhiz.pacify.model.PMarker;

public class CheckTargetFileExist implements PMarkerCheck {

	public List<Defect> checkForErrors(PMarker pfListEntity) {
		List<Defect> defects = new ArrayList<Defect>();

		for (PProperty pproperty : pfListEntity.getProperties()) {
			for (PFile pfile : pproperty.getFiles()) {
				java.io.File file = pfListEntity.getAbsoluteFileFor(pfile);
				if (file.exists() && file.isFile()) {
					continue;
				}
				Defect defect = new TargetFileDoesNotExistDefect(pfListEntity, pproperty, pfile);
				defects.add(defect);
			}
		}

		return defects;
	}
}
