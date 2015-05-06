package com.geewhiz.pacify.mavenplugin;

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

import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import com.geewhiz.pacify.defect.Defect;
import com.geewhiz.pacify.resolver.BasePropertyResolver;

public class MavenPropertyContainer extends BasePropertyResolver {

	Properties properties;
	String encoding;

	public MavenPropertyContainer(Properties properties, String encoding) {
		this.properties = properties;
		this.encoding = encoding;
	}

	public boolean containsProperty(String key) {
		return properties.containsKey(key);
	}

	public String getPropertyValue(String key) {
		if (containsProperty(key)) {
			return properties.getProperty(key);
		}
		throw new IllegalArgumentException("Property [" + key + "] not defined within maven... Aborting!");
	}

	public Set<String> getProperties() {
		Set<String> result = new TreeSet<String>();

		for (Enumeration<Object> enumerator = properties.keys(); enumerator.hasMoreElements();) {
			result.add((String) enumerator.nextElement());
		}

		return result;
	}

	public List<Defect> checkForDuplicateEntry() {
		return Collections.emptyList();
	}

	public String getPropertyResolverDescription() {
		return "maven";
	}

	public String getEncoding() {
		return encoding;
	}

	@Override
	public String getBeginToken() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getEndToken() {
		// TODO Auto-generated method stub
		return null;
	}

}
