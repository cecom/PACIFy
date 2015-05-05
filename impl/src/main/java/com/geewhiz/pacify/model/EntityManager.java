package com.geewhiz.pacify.model;

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

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;

import com.geewhiz.pacify.checker.PacifyChecker;
import com.geewhiz.pacify.common.logger.Log;
import com.geewhiz.pacify.defect.Defect;
import com.geewhiz.pacify.model.utils.PacifyFilesFinder;
import com.geewhiz.pacify.property.PropertyResolveManager;
import com.geewhiz.pacify.replacer.PropertyPFReplacer;

public class EntityManager {

	private File startPath;
	private List<PMarker> pacifyList;

	Logger logger = Log.getInstance();

	public EntityManager(File startPath) {
		this.startPath = startPath;
	}

	public int getPMarkerCount() {
		return getPMarkers().size();
	}

	public List<Defect> validate(PropertyResolveManager propertyResolveManager) {
		PacifyChecker pacifyChecker = new PacifyChecker(propertyResolveManager);

		List<Defect> defects = new ArrayList<Defect>();
		for (PMarker pMarker : getPMarkers()) {
			defects.addAll(pacifyChecker.check(pMarker));
		}

		return defects;
	}

	public List<Defect> doReplacement(PropertyResolveManager propertyResolveManager) {
		List<Defect> defects = new ArrayList<Defect>();
		for (PMarker pMarker : getPMarkers()) {
			logger.info("====== Replacing stuff which is configured in [" + pMarker.getFile().getPath()
			        + "] ...");
			PropertyPFReplacer propertyReplacer = new PropertyPFReplacer(propertyResolveManager, pMarker);
			defects.addAll(propertyReplacer.replace());
		}
		return defects;
	}

	public List<PMarker> getPMarkers() {
		if (pacifyList != null) {
			return pacifyList;
		}

		pacifyList = new ArrayList<PMarker>();

		List<File> pacifyFiles = new PacifyFilesFinder(startPath).getPacifyFiles();

		JAXBContext jaxbContext;
		try {
			jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
		} catch (JAXBException e) {
			throw new RuntimeException("Couldn't create jaxbContext", e);
		}

		for (File file : pacifyFiles) {
			try {
				Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
				PMarker pMarker = (PMarker) jaxbUnmarshaller.unmarshal(file);
				pMarker.setFile(file);
				pacifyList.add(pMarker);
			} catch (Exception e) {
				throw new RuntimeException("Couldn't read xml file [" + file.getPath() + "].", e);
			}
		}

		return pacifyList;
	}
}