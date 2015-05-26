package com.geewhiz.pacify.managers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.apache.tools.ant.types.FilterSet;

import com.geewhiz.pacify.defect.Defect;
import com.geewhiz.pacify.resolver.PropertyResolver;
import com.google.inject.Inject;

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

public class PropertyResolveManager {

	Set<PropertyResolver> propertyResolverList;

	@Inject
	public PropertyResolveManager(Set<PropertyResolver> propertyResolverList) {
		this.propertyResolverList = propertyResolverList;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		Iterator<PropertyResolver> iter = propertyResolverList.iterator();
		while (iter.hasNext()) {
			PropertyResolver propertyResolver = iter.next();
			sb.append(propertyResolver.getPropertyResolverDescription());
			if (iter.hasNext()) {
				sb.append(",");
			}
		}
		return sb.toString();
	}

	public Set<String> getProperties() {
		Set<String> result = new TreeSet<String>();

		for (PropertyResolver propertyResolver : propertyResolverList) {
			result.addAll(propertyResolver.getProperties());
		}

		return result;
	}

	public String getPropertyValue(String property) {
		return getPropertyValue(property, new TreeSet<String>());
	}

	private String getPropertyValue(String property, Set<String> propertyCycleDetector) {
		for (PropertyResolver propertyResolver : propertyResolverList) {
			if (!propertyResolver.containsProperty(property)) {
				continue;
			}

			if (propertyResolver.propertyUsesToken(property)) {
				propertyCycleDetector.add(property);
				return replaceTokens(propertyResolver, property, propertyCycleDetector);
			}
			return propertyResolver.getPropertyValue(property);
		}
		throw new IllegalArgumentException("Property [" + property + "] not found in any resolver!");
	}

	private String replaceTokens(PropertyResolver propertyResolver, String property, Set<String> propertyCycleDetector) {
		FilterSet filterSet = propertyResolver.createFilterSet();
		for (String reference : propertyResolver.getReferencedProperties(property)) {
			if (propertyCycleDetector.contains(reference)) {
				throw new RuntimeException("You have a cycle reference between property [" + property + "] and ["
				        + reference + "].");
			}

			propertyCycleDetector.add(reference);
			filterSet.addFilter(reference, getPropertyValue(reference, propertyCycleDetector));
		}

		String valueWithToken = propertyResolver.getPropertyValue(property);
		return filterSet.replaceTokens(valueWithToken);
	}

	public boolean containsProperty(String name) {
		for (PropertyResolver propertyResolver : propertyResolverList) {
			if (propertyResolver.containsProperty(name)) {
				return true;
			}
		}
		return false;
	}

	public Collection<Defect> checkForDuplicateEntry() {
		Collection<Defect> result = new ArrayList<Defect>();
		for (PropertyResolver propertyResolver : propertyResolverList) {
			result.addAll(propertyResolver.checkForDuplicateEntry());
		}
		return result;
	}
}
