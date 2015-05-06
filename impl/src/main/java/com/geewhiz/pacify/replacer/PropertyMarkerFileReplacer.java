package com.geewhiz.pacify.replacer;

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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;

import org.apache.tools.ant.types.FilterSet;
import org.apache.tools.ant.types.FilterSetCollection;
import org.apache.tools.ant.util.FileUtils;
import org.slf4j.Logger;

import com.geewhiz.pacify.checks.impl.CheckForNotReplacedTokens;
import com.geewhiz.pacify.common.logger.Log;
import com.geewhiz.pacify.defect.Defect;
import com.geewhiz.pacify.model.PFile;
import com.geewhiz.pacify.model.PMarker;
import com.geewhiz.pacify.model.PProperty;
import com.geewhiz.pacify.property.PropertyResolveManager;
import com.geewhiz.pacify.utils.Utils;

public class PropertyMarkerFileReplacer {

	private PropertyResolveManager propertyResolveManager;
	private PMarker pMarker;

	Logger logger = Log.getInstance();

	public PropertyMarkerFileReplacer(PropertyResolveManager propertyResolveManager, PMarker pMarker) {
		this.propertyResolveManager = propertyResolveManager;
		this.pMarker = pMarker;
	}

	public List<Defect> replace() {
		List<Defect> defects = new ArrayList<Defect>();
		for (PFile pfile : pMarker.getPFiles()) {

			FilterSetCollection filterSetCollection = getFilterSetCollection(pfile);

			java.io.File file = pMarker.getAbsoluteFileFor(pfile);
			java.io.File tmpFile = new java.io.File(file.getParentFile(), file.getName() + "_tmp");

			try {
				String encoding = Utils.getEncoding(file);
				logger.info("Using  encoding [" + encoding + "] for  File  [" + file.getAbsolutePath() + "]");
				FileUtils.getFileUtils().copyFile(file, tmpFile, filterSetCollection, true, true, encoding);
				if (!file.delete()) {
					throw new RuntimeException("Couldn't delete file [" + file.getPath() + "]... Aborting!");
				}
				if (!tmpFile.renameTo(file)) {
					throw new RuntimeException("Couldn't rename filtered file from [" + tmpFile.getPath() + "] to ["
					        + file.getPath() + "]... Aborting!");
				}
				CheckForNotReplacedTokens checker = new CheckForNotReplacedTokens();
				defects.addAll(checker.checkForErrors(file));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return defects;
	}

	private FilterSetCollection getFilterSetCollection(PFile pfile) {
		FilterSet filterSet = getFilterSet(pfile);

		FilterSetCollection executionFilters = new FilterSetCollection();
		executionFilters.addFilterSet(filterSet);

		return executionFilters;
	}

	private FilterSet getFilterSet(PFile pfile) {
		FilterSet filterSet = new FilterSet();

		filterSet.setBeginToken(PropertyFileReplacer.BEGIN_TOKEN);
		filterSet.setEndToken(PropertyFileReplacer.END_TOKEN);

		List<PProperty> pproperties = pMarker.getPPropertiesForFile(pfile);

		for (PProperty pproperty : pproperties) {
			String propertyName = pproperty.getName();
			String propertyValue = propertyResolveManager.getPropertyValue(propertyName);

			if (pproperty.isConvertBackslashToSlash()) {
				String convertedString = propertyValue;
				convertedString = propertyValue.replace('\\', '/');
				logger.info(" Converting backslashes [" + propertyValue + "] to slashes [" + convertedString + "]");
				propertyValue = convertedString;
			}

			filterSet.addFilter(propertyName, propertyValue);

			// needed for checking that we don't have a property which references another property, which references
			// this property (cycle)
			Set<String> propertyResolvePath = new TreeSet<String>();
			propertyResolvePath.add(propertyName);

			// if a property contains another property which is not in the marker file, we have to add the other
			// property too.
			for (String referencedPropertyId : getAllReferencedPropertyIds(propertyResolvePath, propertyName,
			        propertyValue)) {
				String referencedValue = propertyResolveManager.getPropertyValue(referencedPropertyId);
				filterSet.addFilter(referencedPropertyId, referencedValue);
			}
		}
		return filterSet;
	}

	private Set<String> getAllReferencedPropertyIds(Set<String> parentPropertyResolvePath, String parentPropertyId,
	        String parentPropertyValue) {
		if (parentPropertyValue == null) {
			return Collections.emptySet();
		}

		Set<String> result = new TreeSet<String>();

		Matcher matcher = PropertyFileReplacer.getPattern("([^}]*)", false).matcher(parentPropertyValue);
		while (matcher.find()) {
			String propertyId = matcher.group(1);

			if (parentPropertyResolvePath.contains(propertyId)) {
				throw new RuntimeException("You have a cycle reference in property [" + parentPropertyId
				        + "] which is used in " +
				        "pflist file [" + pMarker.getFile().getAbsolutePath() + "]. Property values loaded from [TODO]");
			}

			result.add(propertyId);

			Set<String> propertyResolvePath = new TreeSet<String>(parentPropertyResolvePath);
			propertyResolvePath.add(propertyId);

			String propertyValue = propertyResolveManager.getPropertyValue(propertyId);
			result.addAll(getAllReferencedPropertyIds(propertyResolvePath, propertyId, propertyValue));
		}
		return result;
	}
}
