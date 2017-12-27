/*-
 * ========================LICENSE_START=================================
 * com.geewhiz.pacify.api
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

package com.geewhiz.pacify.defect;

import com.geewhiz.pacify.defect.DefectException;
import com.geewhiz.pacify.model.PFile;

public class NotReplacedPropertyDefect extends DefectException {

    private static final long serialVersionUID = 1L;

    private String            propertyId;

    public NotReplacedPropertyDefect(PFile pFile, String propertyId) {
        super(pFile);
        this.propertyId = propertyId;
    }

    @Override
    public String getDefectMessage() {
        return super.getDefectMessage() + String.format("\n\t[Property=%s]", getPropertyId());
    }

    public String getPropertyId() {
        return propertyId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((propertyId == null) ? 0 : propertyId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        NotReplacedPropertyDefect other = (NotReplacedPropertyDefect) obj;
        if (propertyId == null) {
            if (other.propertyId != null) {
                return false;
            }
        } else if (!propertyId.equals(other.propertyId)) {
            return false;
        }
        return true;
    }

}
