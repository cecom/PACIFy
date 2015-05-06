package com.geewhiz.pacify.property.resolver.fileresolver;

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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import com.geewhiz.pacify.defect.Defect;
import com.geewhiz.pacify.defect.PropertyDuplicateDefinedInPropertyFileDefect;
import com.geewhiz.pacify.resolver.BasePropertyResolver;
import com.geewhiz.pacify.utils.FileUtils;
import com.geewhiz.pacify.utils.Utils;

public class FilePropertyResolver extends BasePropertyResolver {

	public static final String IMPORT_STRING = "#!import";

	private URL propertyFileURL;

	private boolean initialized = false;
	private Properties localProperties;
	private Properties properties;
	private String fileEncoding;
	private List<FilePropertyResolver> parentFileProperties = new ArrayList<FilePropertyResolver>();
	private String beginToken = "%{";
	private String endToken = "}";

	public FilePropertyResolver(URL propertyFileURL) {
		this.propertyFileURL = propertyFileURL;
		this.fileEncoding = Utils.getEncoding(propertyFileURL);
	}

	@Override
	public String getEncoding() {
		return fileEncoding;
	}

	public boolean containsProperty(String key) {
		return getPropertyValue(key) != null;
	}

	public String getPropertyValue(String key) {
		return getFileProperties().getProperty(key);
	}

	/**
	 * @return the localProperties for this instance
	 */
	public Properties getLocalProperties() {
		if (!initialized) {
			initialize();
		}
		return localProperties;
	}

	public Set<String> getProperties() {
		if (!initialized) {
			initialize();
		}

		Set<String> result = new TreeSet<String>();

		for (Enumeration<Object> enumerator = properties.keys(); enumerator.hasMoreElements();) {
			result.add((String) enumerator.nextElement());
		}

		return result;
	}

	/**
	 * @return the localProperties for this instance and its parents.
	 */
	public Properties getFileProperties() {
		if (!initialized) {
			initialize();
		}
		return properties;
	}

	public String getPropertyResolverDescription() {
		return getPropertyFileURL().toString();
	}

	public List<Defect> checkForDuplicateEntry() {
		List<Defect> defects = new ArrayList<Defect>();

		Set<String> propertyIds = new HashSet<String>();

		for (String line : FileUtils.getFileAsLines(getPropertyFileURL())) {
			if (line.startsWith("#")) {
				continue;
			}
			if (line.trim().isEmpty()) {
				continue;
			}

			String[] split = line.split("=");
			String propertyId = split[0];
			boolean couldBeAdded = propertyIds.add(propertyId);
			if (!couldBeAdded) {
				Defect defect = new PropertyDuplicateDefinedInPropertyFileDefect(propertyId, this);
				defects.add(defect);
			}
		}

		for (FilePropertyResolver parentFilePropertyContainer : getParentPropertyFileProperties()) {
			defects.addAll(parentFilePropertyContainer.checkForDuplicateEntry());
		}
		return defects;
	}

	public URL getPropertyFileURL() {
		return propertyFileURL;
	}

	public List<FilePropertyResolver> getParentPropertyFileProperties() {
		return parentFileProperties;
	}

	private void initialize() {
		initialized = true;
		loadPropertyFile(this);

		properties = new Properties();
		for (FilePropertyResolver parentFilePropertyContainer : getParentPropertyFileProperties()) {
			Properties parentProperties = parentFilePropertyContainer.getFileProperties();
			properties.putAll(parentProperties);
		}
		properties.putAll(localProperties);
	}

	protected void setLocalProperties(Properties localProperties) {
		this.localProperties = localProperties;
	}

	protected void addParentPropertyFile(FilePropertyResolver parent) {
		parent.initialize();
		parentFileProperties.add(parent);
	}

	public void loadPropertyFile(FilePropertyResolver filePropertyResolver) {
		InputStreamReader isr = null;
		try {
			URL propertyFileURL = filePropertyResolver.getPropertyFileURL();

			isr = getInputStreamReaderFor(propertyFileURL);
			BufferedReader br = new BufferedReader(isr);

			ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(byteArray, getEncoding()));

			for (String line; (line = br.readLine()) != null;) {
				if (line.startsWith(FilePropertyResolver.IMPORT_STRING)) {
					String[] includes = line.substring(FilePropertyResolver.IMPORT_STRING.length()).trim().split(" ");
					for (String include : includes) {
						URL parentPropertyFileURL = new URL(propertyFileURL, include);
						FilePropertyResolver parentFilePropertyContainer = new FilePropertyResolver(
						        parentPropertyFileURL);
						filePropertyResolver.addParentPropertyFile(parentFilePropertyContainer);
					}
					continue;
				}
				bw.write(line);
				bw.newLine();
			}
			bw.flush();

			Properties properties = new Properties();
			properties.load(new StringReader(byteArray.toString(getEncoding())));
			filePropertyResolver.setLocalProperties(properties);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		finally {
			if (isr != null) {
				try {
					isr.close();
				} catch (IOException ignored) {
				}
			}
		}
	}

	private InputStreamReader getInputStreamReaderFor(URL propertyFilePathURL) {
		InputStreamReader result;
		try {
			result = new InputStreamReader(propertyFilePathURL.openStream(), getEncoding());
		} catch (IOException e) {
			throw new RuntimeException("Couldn't find resource [" + propertyFilePathURL + "] in classpath.", e);
		}
		return result;
	}

	public void setBeginToken(String beginToken) {
		this.beginToken = beginToken;
	}

	public String getBeginToken() {
		return beginToken;
	}

	public void setEndToken(String endToken) {
		this.endToken = endToken;
	}

	public String getEndToken() {
		return endToken;
	}

}
