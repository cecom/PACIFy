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

import org.jvnet.jaxb2_commons.lang.EqualsStrategy;
import org.jvnet.jaxb2_commons.lang.HashCodeStrategy;
import org.jvnet.jaxb2_commons.lang.ToStringStrategy;
import org.jvnet.jaxb2_commons.locator.ObjectLocator;

public abstract class PMarkerBase {

	private File file;

	public void setFile(java.io.File file) {
		this.file = file;
	}

	public java.io.File getFile() {
		return file;
	}

	public java.io.File getFolder() {
		return file.getParentFile();
	}

	public java.io.File getAbsoluteFileFor(PFile pfile) {
		return new java.io.File(getFolder(), pfile.getPath());
	}

	public abstract List<PProperty> getProperties();

	public List<PFile> getPFiles() {
		List<PFile> result = new ArrayList<PFile>();
		for (PProperty pproperty : getProperties()) {
			for (PFile pfile : pproperty.getFiles()) {
				if (result.contains(pfile)) {
					continue;
				}
				result.add(pfile);
			}
		}
		return result;
	}

	public List<PProperty> getPfPropertyEntitiesForPFFileEntity(PFile pfile) {
		List<PProperty> result = new ArrayList<PProperty>();
		for (PProperty pproperty : getProperties()) {
			if (pproperty.getFiles().contains(pfile)) {
				result.add(pproperty);
			}
		}
		return result;
	}

	public boolean equals(ObjectLocator thisLocator, ObjectLocator thatLocator, Object object, EqualsStrategy strategy) {
		return file.equals(((PMarker) object).getFile());
	}

	public int hashCode(ObjectLocator locator, HashCodeStrategy strategy) {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((file == null) ? 0 : file.hashCode());
		return result;
	}

	public StringBuilder appendFields(ObjectLocator locator, StringBuilder buffer, ToStringStrategy strategy) {
		return strategy.appendField(locator, this, "file", buffer, file);
	}
}
