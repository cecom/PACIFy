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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Set;
import java.util.TreeSet;

import com.geewhiz.pacify.managers.PropertyResolveManager;
import com.geewhiz.pacify.utils.Utils;
import com.google.inject.Inject;
import com.marzapower.loggable.Log;
import com.marzapower.loggable.Loggable;

@Loggable(loggerName = "com.geewhiz.pacify")
public class CreatePropertyFile {

    public enum OutputType {
        Stdout, File
    }

    private PropertyResolveManager propertyResolveManager;
    private OutputType             outputType;
    private File                   targetFile;
    private String                 outputEncoding;

    @Inject
    public CreatePropertyFile(PropertyResolveManager propertyResolveManager) {
        this.propertyResolveManager = propertyResolveManager;
    }

    public void writeTo() {
        Log.get().info("== Executing CreatePropertyFile [Version=" + Utils.getJarVersion() + "]");
        Log.get().info("   [PropertyResolver=" + propertyResolveManager.toString() + "]");

        if (getOutputType() == OutputType.File) {
            Log.get().info("   [TargetFile=" + getTargetFile().getPath() + "]");
        }

        if (getOutputType() == OutputType.Stdout) {
            writeToStdout();
        } else if (getOutputType() == OutputType.File) {
            writeToFile();
        } else {
            throw new IllegalArgumentException("OutputType not implemented! [" + getOutputType() + "]");
        }
        Log.get().info("== Successfully finished");
    }

    private void writeToFile() {
        if (targetFile.exists()) {
            throw new IllegalArgumentException("File [" + targetFile.getAbsolutePath() + "] allready exists.");
        }

        PrintWriter writer = null;
        try {
            writer = new PrintWriter(targetFile, "utf-8");
            for (String line : getPropertyLines()) {
                writer.println(line);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        finally {
            if (writer != null) {
                writer.close();
            }
        }

    }

    private void writeToStdout() {
        for (String line : getPropertyLines()) {
            System.out.println(line);
        }
    }

    private Set<String> getPropertyLines() {
        Set<String> result = new TreeSet<String>();
        for (String property : propertyResolveManager.getProperties()) {
            String propertyValue = propertyResolveManager.getPropertyValue(property);
            String line = property + "=" + propertyValue;
            result.add(line);
        }
        return result;
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
