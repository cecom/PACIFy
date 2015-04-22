package com.geewhiz.pacify.property;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Properties;
import java.util.Set;

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

	public Properties getProperties() {
		Properties result = new Properties();

		for (PropertyResolver propertyResolver : new SetReverser<PropertyResolver>(propertyResolverList)) {
			result.putAll(propertyResolver.getProperties());
		}

		return result;
	}

	public String getPropertyValue(String name) {
		for (PropertyResolver propertyResolver : propertyResolverList) {
			if (propertyResolver.containsProperty(name)) {
				return propertyResolver.getPropertyValue(name);
			}
		}

		throw new IllegalArgumentException("Property [" + name + "] not found in any resolver!");
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

	class SetReverser<T> implements Iterable<T> {
		private ListIterator<T> listIterator;

		public SetReverser(Set<T> set) {
			List<T> list = new ArrayList<T>(set);
			this.listIterator = list.listIterator(list.size());
		}

		public Iterator<T> iterator() {
			return new Iterator<T>() {

				public boolean hasNext() {
					return listIterator.hasPrevious();
				}

				public T next() {
					return listIterator.previous();
				}

				public void remove() {
					listIterator.remove();
				}

			};
		}

	}

}
