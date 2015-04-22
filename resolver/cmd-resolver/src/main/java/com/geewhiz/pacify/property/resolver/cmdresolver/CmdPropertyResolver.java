package com.geewhiz.pacify.property.resolver.cmdresolver;

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
import java.util.Properties;

import com.geewhiz.pacify.defect.Defect;
import com.geewhiz.pacify.resolver.PropertyResolver;

public class CmdPropertyResolver implements PropertyResolver {

	private Properties properties;

	public CmdPropertyResolver(Properties properties) {
		this.properties = properties;
	}

	@Override
	public boolean containsProperty(String key) {
		return getProperties().containsKey(key);
	}

	@Override
	public String getPropertyValue(String key) {
		return getProperties().getProperty(key);
	}

	@Override
	public List<Defect> checkForDuplicateEntry() {
		return new ArrayList<Defect>();
	}

	@Override
	public Properties getProperties() {
		return properties;
	}

	@Override
	public String getEncoding() {
		return "utf-8";
	}

	@Override
	public String getPropertyResolverDescription() {
		return "CommandLine";
	}
}
