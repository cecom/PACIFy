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

import com.geewhiz.pacify.resolver.PropertyResolver;

public class PropertyDuplicateDefinedInPropertyFileDefect extends DefectException {

    private static final long serialVersionUID = 1L;

    private String            property;
    private PropertyResolver  propertyResolver;

    public PropertyDuplicateDefinedInPropertyFileDefect(String property, PropertyResolver propertyResolver) {
        super();
        this.property = property;
        this.propertyResolver = propertyResolver;
    }

    @Override
    public String getDefectMessage() {
        return String.format("PropertyDuplicateDefinedInPropertyFileDefect:\n\t[Resolver=[%s]]\n\t[Property=%s]",
                propertyResolver.getPropertyResolverDescription(),
                property);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((property == null) ? 0 : property.hashCode());
        result = prime * result + ((propertyResolver == null) ? 0 : propertyResolver.hashCode());
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
        PropertyDuplicateDefinedInPropertyFileDefect other = (PropertyDuplicateDefinedInPropertyFileDefect) obj;
        if (property == null) {
            if (other.property != null) {
                return false;
            }
        } else if (!property.equals(other.property)) {
            return false;
        }
        if (propertyResolver == null) {
            if (other.propertyResolver != null) {
                return false;
            }
        } else if (!propertyResolver.equals(other.propertyResolver)) {
            return false;
        }
        return true;
    }

}
