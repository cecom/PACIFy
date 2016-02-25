package com.geewhiz.pacify.defect;

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

import com.geewhiz.pacify.model.PArchive;
import com.geewhiz.pacify.model.PFile;
import com.geewhiz.pacify.model.PMarker;
import com.geewhiz.pacify.model.PProperty;

public class PropertyNotDefinedInResolverDefect extends DefectException {

    private static final long serialVersionUID = 1L;

    private String            resolvers;

    public PropertyNotDefinedInResolverDefect(PMarker pMarker, PFile pFile, PProperty pProperty, String resolvers) {
        super(pMarker, pFile, pProperty);
        this.resolvers = resolvers;
    }

    public PropertyNotDefinedInResolverDefect(PMarker pMarker, PArchive pArchive, PFile pFile, PProperty pProperty, String resolvers) {
        super(pMarker, pArchive, pFile, pProperty);
        this.resolvers = resolvers;
    }

    @Override
    public String getDefectMessage() {
        return super.getDefectMessage() +
                String.format("\n\t[resolvers=%s]", resolvers);
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
