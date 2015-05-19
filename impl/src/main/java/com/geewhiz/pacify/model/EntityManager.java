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

import com.geewhiz.pacify.model.utils.PacifyFilesFinder;

public class EntityManager {

	private File startPath;
	private List<PMarker> pMarkers;

	public EntityManager(File startPath) {
		this.startPath = startPath;
	}

	public int getPMarkerCount() {
		return getPMarkers().size();
	}

	public List<PMarker> getPMarkers() {
		if (pMarkers != null) {
			return pMarkers;
		}

		pMarkers = new ArrayList<PMarker>();

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
				pMarkers.add(pMarker);
			} catch (Exception e) {
				throw new RuntimeException("Couldn't read xml file [" + file.getPath() + "].", e);
			}
		}

		return pMarkers;
	}
}
