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
import java.util.Enumeration;

import org.slf4j.Logger;

import com.geewhiz.pacify.common.logger.Log;
import com.geewhiz.pacify.property.PropertyResolveManager;
import com.geewhiz.pacify.replacer.PropertyFileReplacer;
import com.geewhiz.pacify.utils.Utils;
import com.google.inject.Inject;

public class Resolver {

	public enum OutputType {
		Stdout, File
	}

	private Logger logger = Log.getInstance();

	private PropertyResolveManager propertyResolveManager;
	private OutputType outputType;
	private File targetFile;
	private String outputEncoding;

	@Inject
	public Resolver(PropertyResolveManager propertyResolveManager) {
		this.propertyResolveManager = propertyResolveManager;
	}

	public void execute() {
		logger.info("== Executing Resolver [Version=" + Utils.getJarVersion() + "]");
		logger.info("     [PropertyResolver=" + propertyResolveManager.toString() + "]");

		if (getOutputType() == OutputType.File) {
			logger.info("     [TargetFile=" + getTargetFile().getPath() + "]");
		}

		File tmpFile = createTempFile();

		// first, lets write all property to the target file
		PrintWriter out = null;
		try {
			out = new PrintWriter(
			        new OutputStreamWriter(new FileOutputStream(tmpFile), getOutputEncodingType()), false);
			for (Enumeration<?> e = propertyResolveManager.getProperties().propertyNames(); e.hasMoreElements();) {
				String propertyId = (String) e.nextElement();
				String propertyValue = propertyResolveManager.getPropertyValue(propertyId);

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
		PropertyFileReplacer replacer = new PropertyFileReplacer(propertyResolveManager);
		replacer.replace(tmpFile);

		if (getOutputType() == OutputType.File) {
			tmpFile.renameTo(getTargetFile());
		} else if (getOutputType() == OutputType.Stdout) {
			writeToStdout(tmpFile, getOutputEncodingType());
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
			return File.createTempFile("pacifytmp", "properties");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public String getOutputEncodingType() {
		return outputEncoding;
	}

	public void setOutputEncoding(String encoding) {
		this.outputEncoding = encoding;
	}

	public OutputType getOutputType() {
		return outputType;
	}

	public void setOutputType(OutputType outputType) {
		this.outputType = outputType;
	}

	public File getTargetFile() {
		return targetFile;
	}

	public void setTargetFile(File file) {
		this.targetFile = file;
	}

}
