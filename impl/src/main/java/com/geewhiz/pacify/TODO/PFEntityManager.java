package com.geewhiz.pacify.TODO;

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

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import com.geewhiz.pacify.checker.PFListChecker;
import com.geewhiz.pacify.defect.Defect;
import com.geewhiz.pacify.logger.Log;
import com.geewhiz.pacify.logger.LogLevel;
import com.geewhiz.pacify.model.PFListEntity;
import com.geewhiz.pacify.model.utils.PFListFilesFinder;
import com.geewhiz.pacify.property.PropertyContainer;
import com.geewhiz.pacify.replacer.PropertyPFReplacer;

public class PFEntityManager {

	private File startPath;
	private List<PFListEntity> pfListEntities;

	public PFEntityManager(File startPath) {
		this.startPath = startPath;
	}

	public int getPFListCount() {
		return getPFLists().size();
	}

	public List<Defect> checkCorrectnessOfPFListFiles(PropertyContainer propertyContainer) {
		PFListChecker pfListChecker = new PFListChecker(propertyContainer);

		List<Defect> defects = new ArrayList<Defect>();
		for (PFListEntity pfListEntity : getPFLists()) {
			defects.addAll(pfListChecker.check(pfListEntity));
		}

		return defects;
	}

	public List<Defect> doReplacement(PropertyContainer propertyContainer) {
		List<Defect> defects = new ArrayList<Defect>();
		for (PFListEntity pfListEntity : getPFLists()) {
			Log.log(LogLevel.INFO, "====== Replacing stuff which is configured in [" + pfListEntity.getFile().getPath()
			        + "] ...");
			PropertyPFReplacer propertyReplacer = new PropertyPFReplacer(propertyContainer, pfListEntity);
			defects.addAll(propertyReplacer.replace());
		}
		return defects;
	}

	public List<PFListEntity> getPFLists() {
		if (pfListEntities == null) {
			pfListEntities = new ArrayList<PFListEntity>();
			List<File> pfListFiles = new PFListFilesFinder(startPath).getPFListFiles();
			Serializer serializer = new Persister();
			for (File file : pfListFiles) {
				try {
					PFListEntity pfListEntity = serializer.read(PFListEntity.class, file);
					pfListEntity.setFile(file);
					pfListEntities.add(pfListEntity);
				} catch (Exception e) {
					throw new RuntimeException("Couldn't read xml file [" + file.getPath() + "].", e);
				}
			}
		}
		return pfListEntities;
	}
}
