package com.geewhiz.pacify.model;

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
import java.util.ArrayList;
import java.util.List;

import org.jvnet.jaxb2_commons.lang.EqualsStrategy;
import org.jvnet.jaxb2_commons.lang.HashCodeStrategy;
import org.jvnet.jaxb2_commons.lang.ToStringStrategy;
import org.jvnet.jaxb2_commons.locator.ObjectLocator;

public abstract class PMarkerBase {

    private File file;

    public void setFile(java.io.File file) {
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    public List<PFile> getPFiles() {
        List<PFile> result = new ArrayList<PFile>();

        for (Object entry : getFilesAndArchives()) {
            if (entry instanceof PFile) {
                result.add((PFile) entry);
            }
        }
        return result;
    }

    public List<PArchive> getPArchives() {
        List<PArchive> result = new ArrayList<PArchive>();

        for (Object entry : getFilesAndArchives()) {
            if (entry instanceof PArchive) {
                result.add((PArchive) entry);
            }
        }
        return result;
    }

    public abstract List<Object> getFilesAndArchives();

    public File getFolder() {
        return file.getParentFile();
    }

    public File getAbsoluteFileFor(PFile pFile) {
        return new File(getFolder(), pFile.getRelativePath());
    }

    public File getAbsoluteFileFor(PArchive pArchive) {
        return new File(getFolder(), pArchive.getRelativePath());
    }

    public String getBeginTokenFor(PFile pFile) {
        return pFile.getBeginToken() != null ? pFile.getBeginToken() : getBeginToken();
    }

    public String getBeginTokenFor(PArchive pArchive, PFile pFile) {
        if (pFile.getBeginToken() != null) {
            return pFile.getBeginToken();
        }
        if (pArchive.getBeginToken() != null) {
            return pArchive.getBeginToken();
        }
        return getBeginToken();
    }

    public String getEndTokenFor(PFile pFile) {
        return pFile.getEndToken() != null ? pFile.getEndToken() : getEndToken();
    }

    public String getEndTokenFor(PArchive pArchive, PFile pFile) {
        if (pFile.getEndToken() != null) {
            return pFile.getEndToken();
        }
        if (pArchive.getEndToken() != null) {
            return pArchive.getEndToken();
        }
        return getEndToken();
    }

    public abstract String getEndToken();

    public abstract String getBeginToken();

    public boolean equals(ObjectLocator thisLocator, ObjectLocator thatLocator, Object object, EqualsStrategy strategy) {
        return file.equals(((PMarker) object).getFile());
    }

    public int hashCode(ObjectLocator locator, HashCodeStrategy strategy) {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((file == null) ? 0 : file.hashCode());
        return result;
    }

    public StringBuilder appendFields(ObjectLocator locator, StringBuilder buffer, ToStringStrategy strategy) {
        return strategy.appendField(locator, this, "file", buffer, file);
    }
}
