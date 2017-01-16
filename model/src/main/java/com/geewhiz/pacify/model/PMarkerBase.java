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
import java.util.ArrayList;
import java.util.List;

import org.jvnet.jaxb2_commons.lang.CopyStrategy;
import org.jvnet.jaxb2_commons.lang.EqualsStrategy;
import org.jvnet.jaxb2_commons.lang.HashCodeStrategy;
import org.jvnet.jaxb2_commons.lang.ToStringStrategy;
import org.jvnet.jaxb2_commons.locator.ObjectLocator;

public abstract class PMarkerBase {

    private File    file;

    private Boolean resolved;

    private Boolean successfullyProcessed;

    public void setFile(java.io.File file) {
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    public File getFolder() {
        return file.getParentFile();
    }

    /**
     * if resolved this model does only contain PFiles.
     * 
     * @return
     */
    public List<PFile> getPFiles() {
        List<PFile> result = new ArrayList<PFile>();

        for (Object entry : getFilesAndArchives()) {
            if (entry instanceof PFile) {
                PFile pFile = (PFile) entry;
                result.add(pFile);
            }
        }

        return result;
    }

    public void setSuccessfullyProcessed(Boolean state) {
        this.successfullyProcessed = state;
    }

    public Boolean isSuccessfullyProcessed() {
        return successfullyProcessed != null && successfullyProcessed;
    }

    public String getXPath() {
        return "/Pacify";
    }

    public abstract List<Object> getFilesAndArchives();

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

    public Object copyTo(ObjectLocator locator, Object draftCopy, CopyStrategy strategy) {
        if (draftCopy instanceof PMarker) {
            ((PMarker) draftCopy).setFile(file);
        }
        return draftCopy;
    }

    public Boolean isResolved() {
        return resolved != null && resolved;
    }

    public void setResolved(Boolean resolved) {
        this.resolved = resolved;
    }
}
