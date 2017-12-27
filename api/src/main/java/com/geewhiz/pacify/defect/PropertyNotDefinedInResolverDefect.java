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
import com.geewhiz.pacify.model.PProperty;

public class PropertyNotDefinedInResolverDefect extends DefectException {

    private static final long serialVersionUID = 1L;

    private String            resolvers;

    public PropertyNotDefinedInResolverDefect(PProperty pProperty, String resolvers) {
        super(pProperty);
        this.resolvers = resolvers;
    }

    @Override
    public String getDefectMessage() {
        return super.getDefectMessage() + String.format("\n\t[resolvers=%s]", resolvers);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((resolvers == null) ? 0 : resolvers.hashCode());
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
        PropertyNotDefinedInResolverDefect other = (PropertyNotDefinedInResolverDefect) obj;
        if (resolvers == null) {
            if (other.resolvers != null) {
                return false;
            }
        } else if (!resolvers.equals(other.resolvers)) {
            return false;
        }
        return true;
    }

}
