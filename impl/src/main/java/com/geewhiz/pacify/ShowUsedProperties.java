/*-
 * ========================LICENSE_START=================================
 * com.geewhiz.pacify.impl
 * %%
 * Copyright (C) 2011 - 2017 Sven Oppermann
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */

package com.geewhiz.pacify;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.geewhiz.pacify.defect.Defect;
import com.geewhiz.pacify.managers.EntityManager;
import com.geewhiz.pacify.model.PFile;
import com.geewhiz.pacify.model.PMarker;
import com.geewhiz.pacify.model.PProperty;
import com.geewhiz.pacify.utils.DefectUtils;
import com.geewhiz.pacify.utils.Utils;



public class ShowUsedProperties {

    public enum OutputType {
        Stdout, File
    }

    private Logger     logger = LogManager.getLogger(ShowUsedProperties.class.getName());

    private File       packagePath;
    private File       targetFile;
    private String     targetEncoding;
    private OutputType outputType;
    private String     outputPrefix;

    public void execute() {
        EntityManager entityManager = new EntityManager(getPackagePath());

        logger.info("== Executing ShowUsedProperties [Version={}]", Utils.getJarVersion());
        logger.info("== Found [{}] pacify marker files", entityManager.getPMarkerCount());

        LinkedHashSet<Defect> defects = entityManager.initialize();
        DefectUtils.abortIfDefectExists(defects);

        if (getOutputType() == OutputType.Stdout) {
            logger.info("== Getting Properties...");
            writeToStdout(entityManager);
        } else if (getOutputType() == OutputType.File) {
            logger.info("   [TargetFile={}]", getTargetFile().getPath());
            logger.info("== Getting Properties...");
            writeToFile(entityManager);
        } else {
            throw new IllegalArgumentException("OutputType not implemented! [" + getOutputType() + "]");
        }

        logger.info("== Successfully finished");
    }

    private void writeToFile(EntityManager entityManager) {
        if (getTargetFile().exists()) {
            throw new IllegalArgumentException("File [" + getTargetFile().getAbsolutePath() + "] allready exists.");
        }

        if (getTargetFile().getParentFile() != null) {
            getTargetFile().getParentFile().mkdirs();
        }

        PrintWriter writer = null;
        try {
            writer = new PrintWriter(targetFile, getOutputEncoding());
            for (String property : getAllProperties(entityManager)) {
                writer.println(getOutputPrefix() + property);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    private void writeToStdout(EntityManager entityManager) {
        for (String usedProperty : getAllProperties(entityManager)) {
            System.out.println(getOutputPrefix() + usedProperty);
        }
    }

    private Set<String> getAllProperties(EntityManager entityManager) {
        Set<String> allUsedProperties = new TreeSet<String>();
        for (PMarker pMarker : entityManager.getPMarkers()) {
            logger.info("   [{}]", pMarker.getFile().getAbsolutePath());

            for (PFile pFile : entityManager.getPFilesFrom(pMarker)) {
                logger.debug("      [Getting properties for file {}]", pFile.getPUri());
                getPFileProperties(allUsedProperties, pFile);
            }

        }
        return allUsedProperties;
    }

    private void getPFileProperties(Set<String> allUsedProperties, PFile pFile) {
        for (PProperty pProperty : pFile.getPProperties()) {
            allUsedProperties.add(pProperty.getName());
        }
    }

    public void setOutputEncoding(String targetEncoding) {
        this.targetEncoding = targetEncoding;
    }

    public String getOutputEncoding() {
        return targetEncoding;
    }

    public void setTargetFile(File targetFile) {
        this.targetFile = targetFile;
    }

    public File getTargetFile() {
        return targetFile;
    }

    private OutputType getOutputType() {
        return outputType;
    }

    public void setOutputType(OutputType outputType) {
        this.outputType = outputType;
    }

    public File getPackagePath() {
        return packagePath;
    }

    public void setPackagePath(File packagePath) {
        this.packagePath = packagePath;
    }

    public void setOutputPrefix(String outputPrefix) {
        this.outputPrefix = outputPrefix;
    }

    public String getOutputPrefix() {
        return outputPrefix;
    }
}
