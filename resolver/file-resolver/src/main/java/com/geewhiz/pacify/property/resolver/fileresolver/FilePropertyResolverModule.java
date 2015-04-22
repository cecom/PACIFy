package com.geewhiz.pacify.property.resolver.fileresolver;

import java.net.URL;
import java.util.Map;

import com.geewhiz.pacify.common.file.FileUtils;
import com.geewhiz.pacify.resolver.PropertyResolver;
import com.geewhiz.pacify.resolver.PropertyResolverModule;
import com.google.inject.Provides;
import com.google.inject.multibindings.Multibinder;

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

public class FilePropertyResolverModule extends PropertyResolverModule {

	Map<String, String> commandLineParameters;

	@Override
	public String getPropertyResolverId() {
		return "FileResolver";
	}

	@Override
	protected void configure() {
		Multibinder<PropertyResolver> resolveBinder = Multibinder.newSetBinder(binder(), PropertyResolver.class);
		resolveBinder.addBinding().to(FilePropertyResolver.class);
	}

	@Override
	public void setCommandLineParameters(Map<String, String> commandLineParameters) {
		this.commandLineParameters = commandLineParameters;
	}

	@Provides
	public FilePropertyResolver createFilePropertyResolver() {
		String file = commandLineParameters.get("file");

		if (file == null) {
			throw new IllegalArgumentException(
			        "The FileResolver need's the file where to read the properties from. Specify it via -DFileResolver.file=<path>");
		}

		URL fileUrl = FileUtils.getFileUrl(commandLineParameters.get("file"));
		return new FilePropertyResolver(fileUrl);
	}
}
