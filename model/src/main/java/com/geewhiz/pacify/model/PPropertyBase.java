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

import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.Unmarshaller;

import org.jvnet.jaxb2_commons.lang.CopyStrategy2;
import org.jvnet.jaxb2_commons.lang.EqualsStrategy2;
import org.jvnet.jaxb2_commons.lang.HashCodeStrategy2;
import org.jvnet.jaxb2_commons.lang.ToStringStrategy2;
import org.jvnet.jaxb2_commons.locator.ObjectLocator;

import com.geewhiz.pacify.defect.DefectRuntimeException;

public abstract class PPropertyBase {

    private PFile          pFile;

    /*
     * this is true, if the property was already resolved
     */
    private boolean        isResolved = false;

    /*
     * the resolved value
     */
    private String         value;

    /*
     * if the property has a reference, here the list of all references which are used
     */
    private Set<PProperty> referencedProperties;

    /*
     * this is true, if something bad has occurred
     */
    private Boolean        successfullyProcessed;

    public PFile getPFile() {
        return pFile;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setIsResolved(Boolean isResolved) {
        this.isResolved = isResolved;
    }

    public boolean isResolved() {
        return isResolved;
    }

    public Set<PProperty> getReferencedProperties() {
        if (referencedProperties == null) {
            referencedProperties = new HashSet<PProperty>();
        }
        return referencedProperties;
    }

    public void addAReference(PProperty reference) {
        getReferencedProperties().add(reference);
    }

    public String getXPath() {
        StringBuffer result = new StringBuffer();
        result.append(pFile.getXPath());
        result.append("/Property[@Name='").append(getName()).append("']");

        return result.toString();
    }

    protected abstract String getName();

    public void setPFile(PFile pFile) {
        this.pFile = pFile;
    }

    public void setSuccessfullyProcessed(Boolean state) {
        this.successfullyProcessed = state;
    }

    public Boolean isSuccessfullyProcessed() {
        return successfullyProcessed != null && successfullyProcessed;
    }

    public String getBeginToken() {
        return getPFile().getBeginToken();
    }

    public String getEndToken() {
        return getPFile().getEndToken();
    }

    public void afterUnmarshal(Unmarshaller unmarshaller, Object parent) {
        if (parent instanceof PFile) {
            pFile = (PFile) parent;
        } else {
            throw new DefectRuntimeException("Wrong Parent [" + parent.getClass().getName() + "]");
        }
    }

    public int hashCode(ObjectLocator locator, HashCodeStrategy2 strategy) {
        // we don't have any attribute in this class so not needed
        return 1;
    }

    public boolean equals(ObjectLocator thisLocator, ObjectLocator thatLocator, Object object, EqualsStrategy2 strategy) {
        // we don't have any attribute in this class so not needed
        return true;
    }

    public StringBuilder appendFields(ObjectLocator locator, StringBuilder buffer, ToStringStrategy2 strategy) {
        // we don't have any attribute in this class so not needed
        return buffer;
    }

    public Object copyTo(ObjectLocator locator, Object draftCopy, CopyStrategy2 strategy) {
        if (draftCopy instanceof PProperty) {
            ((PProperty) draftCopy).setPFile(pFile);
        }
        return draftCopy;
    }

}
