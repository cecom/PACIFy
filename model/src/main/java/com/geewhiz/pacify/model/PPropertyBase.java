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

import javax.xml.bind.Unmarshaller;

import org.jvnet.jaxb2_commons.lang.CopyStrategy;
import org.jvnet.jaxb2_commons.lang.EqualsStrategy;
import org.jvnet.jaxb2_commons.lang.HashCodeStrategy;
import org.jvnet.jaxb2_commons.lang.ToStringStrategy;
import org.jvnet.jaxb2_commons.locator.ObjectLocator;

import com.geewhiz.pacify.defect.DefectRuntimeException;



public abstract class PPropertyBase {

    private PFile pFile;

    public PFile getPFile() {
        return pFile;
    }

    protected PFile setPFile(PFile pFile) {
        return pFile;
    }

    public void afterUnmarshal(Unmarshaller unmarshaller, Object parent) {
        if (parent instanceof PFile) {
            pFile = (PFile) parent;
        } else {
            throw new DefectRuntimeException("Wrong Parent [" + parent.getClass().getName() + "]");
        }
    }

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
        if (draftCopy instanceof PProperty) {
            ((PProperty) draftCopy).setPFile(pFile);
        }
        return draftCopy;
    }

}
