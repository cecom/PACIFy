package com.geewhiz.pacify;

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
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.util.EnumMap;
import java.util.Enumeration;

import org.slf4j.Logger;

import com.geewhiz.pacify.common.logger.Log;
import com.geewhiz.pacify.property.FilePropertyContainer;
import com.geewhiz.pacify.property.PropertyContainer;
import com.geewhiz.pacify.replacer.PropertyFileReplacer;
import com.geewhiz.pacify.utils.Utils;

public class Resolver {

	public enum Parameter {
		PropertyFileURL, TargetFile, OutputType
	}

	public enum OutputType {
		Stdout, File
	}

	private Logger logger = Log.getInstance();
	private EnumMap<Parameter, Object> propertyMap;

	public Resolver(EnumMap<Parameter, Object> propertyMap) {
		this.propertyMap = propertyMap;

		logger.info("== Executing CreateResultPropertyFile [Version=" + Utils.getJarVersion() + "]");
		logger.info("     [PropertyFileURL=" + getPropertyFileURL().getPath() + "]");
		if (getOutputType() == OutputType.File) {
			logger.info("     [TargetFile=" + getTargetFile().getPath() + "]");
		}
	}

	public void create() {
		PropertyContainer propertyContainer = new FilePropertyContainer(getPropertyFileURL());

		File tmpFile = createTempFile();

		// first, lets write all property to the target file
		PrintWriter out = null;
		try {
			out = new PrintWriter(
			        new OutputStreamWriter(new FileOutputStream(tmpFile), propertyContainer.getEncoding()), false);
			for (Enumeration e = propertyContainer.getProperties().propertyNames(); e.hasMoreElements();) {
				String propertyId = (String) e.nextElement();
				String propertyValue = propertyContainer.getPropertyValue(propertyId);

				// i don't use propertyContainer.getProperties.store(..) because he quotes
				out.println(propertyId + "=" + propertyValue);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		finally {
			if (out != null) {
				out.close();
			}
		}

		// second, replace all in place used variables e.g. bla=%{foo}%{bar}
		PropertyFileReplacer replacer = new PropertyFileReplacer(propertyContainer);
		replacer.replace(tmpFile);

		if (getOutputType() == OutputType.File) {
			tmpFile.renameTo(getTargetFile());
		} else if (getOutputType() == OutputType.Stdout) {
			writeToStdout(tmpFile, propertyContainer.getEncoding());
		} else {
			throw new IllegalArgumentException("OutputType not implemented! [" + getOutputType() + "]");
		}
	}

	private void writeToStdout(File tmpFile, String encoding) {
		BufferedWriter bw = null;
		BufferedReader br = null;
		try {
			bw = new BufferedWriter(new OutputStreamWriter(System.out, encoding));
			br = new BufferedReader(new FileReader(tmpFile));

			for (String line; (line = br.readLine()) != null;) {
				bw.write(line);
				bw.newLine();
			}
			bw.flush();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		finally {
			try {
				br.close();
				// you should not close bw because you close the maven stdout too
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

	}

	private File createTempFile() {
		try {
			return File.createTempFile("pftmp", "properties");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private File getTargetFile() {
		return (File) propertyMap.get(Parameter.TargetFile);
	}

	private URL getPropertyFileURL() {
		return (URL) propertyMap.get(Parameter.PropertyFileURL);
	}

	private OutputType getOutputType() {
		return (OutputType) propertyMap.get(Resolver.Parameter.OutputType);
	}

}
