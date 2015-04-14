package com.geewhiz.pacify.defect;

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

import com.geewhiz.pacify.model.PFile;
import com.geewhiz.pacify.model.PProperty;
import com.geewhiz.pacify.model.PMarker;

public class PropertyDoesNotExistInTargetFileDefect implements Defect {

	private PMarker pMarker;
	private PProperty pproperty;
	private PFile pfile;

	public PropertyDoesNotExistInTargetFileDefect(PMarker pMarker, PProperty pproperty,
	    PFile pfile) {
		this.pMarker = pMarker;
		this.pproperty = pproperty;
		this.pfile = pfile;
	}

	public String getDefectMessage() {
		return "Property [" + pproperty.getName() + "] which is defined in [TODO] couldn't be found in file ["
		        + pMarker.getAbsoluteFileFor(pfile).getPath() + "]";
	}
}