package com.geewhiz.pacify.model;

import java.io.File;

import javax.xml.bind.Unmarshaller;

import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.jvnet.jaxb2_commons.lang.CopyStrategy;
import org.jvnet.jaxb2_commons.lang.EqualsStrategy;
import org.jvnet.jaxb2_commons.lang.HashCodeStrategy;
import org.jvnet.jaxb2_commons.lang.ToStringStrategy;
import org.jvnet.jaxb2_commons.locator.ObjectLocator;

import com.geewhiz.pacify.defect.DefectRuntimeException;

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

public abstract class PArchiveBase {

    private PMarker pMarker;

    public String getInternalType() {
        int idx = getRelativePath().lastIndexOf(".");
        return getRelativePath().substring(idx + 1);
    }

    public PMarker getPMarker() {
        return pMarker;
    }

    public String getPUri() {
        StringBuffer sb = new StringBuffer();
        sb.append(getRelativePath());
        return sb.toString();
    }

    protected void setPMarker(PMarker pMarker) {
        this.pMarker = pMarker;
    }

    public String getType() {
        String type = getInternalType();

        if ("jar".equalsIgnoreCase(type)) {
            return ArchiveStreamFactory.JAR;
        }
        if ("war".equalsIgnoreCase(type)) {
            return ArchiveStreamFactory.JAR;
        }
        if ("ear".equalsIgnoreCase(type)) {
            return ArchiveStreamFactory.JAR;
        }
        if ("zip".equalsIgnoreCase(type)) {
            return ArchiveStreamFactory.ZIP;
        }
        if ("tar".equalsIgnoreCase(type)) {
            return ArchiveStreamFactory.TAR;
        }
        return type;
    }

    public String getBeginToken() {
        return getInternalBeginToken() != null ? getInternalBeginToken() : getPMarker().getBeginToken();
    }

    public String getEndToken() {
        return getInternalEndToken() != null ? getInternalEndToken() : getPMarker().getEndToken();
    }

    public File getFile() {
        return new File(getPMarker().getFolder(), getRelativePath());
    }

    public abstract String getInternalEndToken();

    public abstract String getInternalBeginToken();

    public abstract String getRelativePath();

    public int hashCode(ObjectLocator locator, HashCodeStrategy strategy) {
        // we don't have any attribute in this class so not needed
        return 1;
    }

    public boolean equals(ObjectLocator thisLocator, ObjectLocator thatLocator, Object object, EqualsStrategy strategy) {
        // we don't have any attribute in this class so not needed
        return true;
    }

    public StringBuilder appendFields(ObjectLocator locator, StringBuilder buffer, ToStringStrategy strategy) {
        // we don't have any attribute in this class so not needed
        return buffer;
    }

    public Object copyTo(ObjectLocator locator, Object draftCopy, CopyStrategy strategy) {
        if (draftCopy instanceof PArchive) {
            ((PArchive) draftCopy).setPMarker(pMarker);
        }
        return draftCopy;
    }

    public void afterUnmarshal(Unmarshaller unmarshaller, Object parent) {
        if (parent instanceof PMarker) {
            pMarker = (PMarker) parent;
        } else {
            throw new DefectRuntimeException("Wrong Parent [" + parent.getClass().getName() + "]");
        }
    }
}
