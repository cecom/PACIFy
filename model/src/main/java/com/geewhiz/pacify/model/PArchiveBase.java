/*-
 * ========================LICENSE_START=================================
 * com.geewhiz.pacify.model
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

package com.geewhiz.pacify.model;

import java.io.File;

import javax.xml.bind.Unmarshaller;

import org.jvnet.jaxb2_commons.lang.CopyStrategy;
import org.jvnet.jaxb2_commons.lang.EqualsStrategy;
import org.jvnet.jaxb2_commons.lang.HashCodeStrategy;
import org.jvnet.jaxb2_commons.lang.ToStringStrategy;
import org.jvnet.jaxb2_commons.locator.ObjectLocator;

import com.geewhiz.pacify.defect.DefectRuntimeException;

public abstract class PArchiveBase {

    private PMarker  pMarker;

    private PArchive pParentArchive;

    /**
     * The physical representation. if this entry is an archive in an archive.
     */
    private File     file;

    private String   archiveType;

    public PMarker getPMarker() {
        if (isArchiveFile())
            return getParentArchive().getPMarker();
        return pMarker;
    }

    public PArchive getParentArchive() {
        return pParentArchive;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public Boolean isArchiveFile() {
        return getParentArchive() != null;
    }

    public String getPUri() {
        StringBuffer sb = new StringBuffer();
        if (getParentArchive() != null)
            sb.append(getParentArchive().getPUri()).append("!");
        sb.append(getRelativePath());
        return sb.toString();
    }

    protected void setPMarker(PMarker pMarker) {
        this.pMarker = pMarker;
    }

    public String getType() {
        return archiveType;
    }

    public void setType(String archiveType) {
        this.archiveType = archiveType;
    }

    public String getBeginToken() {
        return getInternalBeginToken() != null ? getInternalBeginToken() : getPMarker().getBeginToken();
    }

    public String getEndToken() {
        return getInternalEndToken() != null ? getInternalEndToken() : getPMarker().getEndToken();
    }

    public String getXPath() {
        StringBuffer result = new StringBuffer();
        if (getParentArchive() != null) {
            result.append(getParentArchive().getXPath());
        } else {
            result.append(getPMarker().getXPath());
        }
        result.append("/Archive[@RelativePath='").append(getRelativePath()).append("']");

        return result.toString();
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
        } else if (parent instanceof PArchive) {
            pParentArchive = (PArchive) parent;
        } else {
            throw new DefectRuntimeException("Wrong Parent [" + parent.getClass().getName() + "]");
        }
    }
}
