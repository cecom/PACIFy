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
import com.geewhiz.pacify.model.PMarker;

public class PropertyHasCycleDefect extends DefectException {

    private static final long serialVersionUID = 1L;

    private String            property;
    private String            cycle;

    public PropertyHasCycleDefect(PMarker pMarker, String property, String cycle) {
        super(pMarker);
        this.property = property;
        this.cycle = cycle;
    }

    public PropertyHasCycleDefect(PMarker pMarker, PArchive pArchive, String property, String cycle) {
        super(pMarker, pArchive);
        this.property = property;
        this.cycle = cycle;
    }

    @Override
    public String getDefectMessage() {
        return super.getDefectMessage() +
                String.format("\n\t[Property=%s]\n\t[cycle=%s]", getProperty(), getCycle());
    }

    public String getCycle() {
        return cycle;
    }

    public String getProperty() {
        return property;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((cycle == null) ? 0 : cycle.hashCode());
        result = prime * result + ((property == null) ? 0 : property.hashCode());
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
        PropertyHasCycleDefect other = (PropertyHasCycleDefect) obj;
        if (cycle == null) {
            if (other.cycle != null) {
                return false;
            }
        } else if (!cycle.equals(other.cycle)) {
            return false;
        }
        if (property == null) {
            if (other.property != null) {
                return false;
            }
        } else if (!property.equals(other.property)) {
            return false;
        }
        return true;
    }

}
