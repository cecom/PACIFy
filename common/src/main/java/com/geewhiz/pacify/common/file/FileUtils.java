package com.geewhiz.pacify.common.file;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

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

public class FileUtils {

	public static URL getFileUrl(String filePath) {
		File file = new File(filePath);
		if (file.exists() && file.isFile()) {
			try {
				return file.toURI().toURL();
			} catch (MalformedURLException e) {
				throw new RuntimeException(e);
			}
		}

		URL url = FileUtils.class.getClassLoader().getResource(filePath);
		if (url != null) {
			return url;
		}

		throw new RuntimeException("Couldn't find property File [" + filePath
		        + "] in Classpath nor absolute... Aborting!");
	}

}
