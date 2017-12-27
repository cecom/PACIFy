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
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Chmod;

import com.geewhiz.pacify.defect.Defect;
import com.geewhiz.pacify.defect.ResolverDefect;
import com.geewhiz.pacify.exceptions.ResolverRuntimeException;
import com.geewhiz.pacify.managers.PropertyResolveManager;
import com.geewhiz.pacify.model.PProperty;
import com.geewhiz.pacify.utils.DefectUtils;
import com.geewhiz.pacify.utils.Utils;
import com.google.inject.Inject;

public class CreatePropertyFile {

    public enum OutputType {
        Stdout, File
    }

    private Logger                 logger = LogManager.getLogger(CreatePropertyFile.class.getName());

    private PropertyResolveManager propertyResolveManager;
    private OutputType             outputType;
    private File                   targetFile;
    private String                 outputEncoding;
    private String                 filemode;
    private String                 outputPrefix;

    @Inject
    public CreatePropertyFile(PropertyResolveManager propertyResolveManager) {
        this.propertyResolveManager = propertyResolveManager;
    }

    public void write() {
        logger.info("== Executing CreatePropertyFile [Version={}]", Utils.getJarVersion());
        logger.info("   [PropertyResolver={}]", propertyResolveManager.toString());

        if (getOutputType() == OutputType.Stdout) {
            writeToStdout();
        } else if (getOutputType() == OutputType.File) {
            logger.info("   [TargetFile={}]", getTargetFile().getPath());
            writeToFile();
        } else {
            throw new IllegalArgumentException("OutputType not implemented! [" + getOutputType() + "]");
        }
        logger.info("== Successfully finished");
    }

    private void writeToFile() {
        if (targetFile.exists()) {
            throw new IllegalArgumentException("File [" + targetFile.getAbsolutePath() + "] allready exists.");
        }

        PrintWriter writer = null;
        try {
            writer = new PrintWriter(targetFile, getOutputEncoding());
            for (String line : getPropertyLines()) {
                writer.println(getOutputPrefix() + line);
            }

            setPermission();

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

    private void writeToStdout() {
        for (String line : getPropertyLines()) {
            System.out.println(getOutputPrefix() + line);
        }
    }

    private void setPermission() {
        logger.debug("Changing file mode to {}", filemode);
        Chmod chmod = new Chmod();
        chmod.setProject(new Project());
        chmod.setFile(targetFile);
        chmod.setPerm(filemode);
        chmod.execute();
    }

    private Set<String> getPropertyLines() {
        Set<String> result = new TreeSet<String>();

        LinkedHashSet<Defect> defects = new LinkedHashSet<Defect>();

        for (String propertyKey : propertyResolveManager.getPropertyKeys()) {
            String protectedPrefix = "";
            if (propertyResolveManager.isProtectedProperty(propertyKey)) {
                protectedPrefix = "*";
            }
            try {

                PProperty pProperty = createPPropertyFor(propertyKey);

                propertyResolveManager.resolveProperty(pProperty);

                String line = protectedPrefix + propertyKey + "=" + pProperty.getValue();
                result.add(line);
            } catch (ResolverRuntimeException re) {
                defects.add(new ResolverDefect(re.getResolver(), re.getMessage()));
                continue;
            }
        }

        DefectUtils.abortIfDefectExists(defects);

        return result;
    }

    public String getOutputEncoding() {
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

    public String getFilemode() {
        return filemode;
    }

    public void setFilemode(String filemode) {
        this.filemode = filemode;
    }

    public void setOutputPrefix(String outputPrefix) {
        this.outputPrefix = outputPrefix;
    }

    public String getOutputPrefix() {
        return outputPrefix;
    }

    private PProperty createPPropertyFor(String propertyKey) {
        PProperty pProperty = new PProperty() {
            @Override
            public String getBeginToken() {
                return "%{";
            }

            @Override
            public String getEndToken() {
                return "}";
            }
        };
        pProperty.setName(propertyKey);
        return pProperty;
    }

}
