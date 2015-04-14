package com.geewhiz.pacify.utils;

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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.mozilla.universalchardet.UniversalDetector;

public class Utils {

	public static String getJarVersion() {
		URL jarURL = Utils.class.getResource("/" + Utils.class.getName().replace(".", "/") + ".class");
		Manifest mf;
		try {
			JarURLConnection jurlConn;
			if (jarURL.getProtocol().equals("file")) {
				return "Not a Jar";
			} else {
				jurlConn = (JarURLConnection) jarURL.openConnection();
			}
			mf = jurlConn.getManifest();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		Attributes attr = mf.getMainAttributes();
		return attr.getValue("Implementation-Version");
	}

	public static String getEncoding(URL fileUrl) {
		try {
			return getEncoding(fileUrl.openStream());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static String getEncoding(File file) {
		try {
			return getEncoding(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	private static String getEncoding(InputStream inputStream) {
		try {
			UniversalDetector detector = new UniversalDetector(null);
			int nread;
			byte[] buf = new byte[4096];

			while ((nread = inputStream.read(buf)) > 0 && !detector.isDone()) {
				detector.handleData(buf, 0, nread);
			}
			detector.dataEnd();

			inputStream.close();

			if (detector.getDetectedCharset() != null) {
				return detector.getDetectedCharset();
			} else {
				return Charset.defaultCharset().name();
			}
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
