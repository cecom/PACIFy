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

import com.geewhiz.pacify.model.PFListEntity;
import com.geewhiz.pacify.model.PFPropertyEntity;
import com.geewhiz.pacify.property.PropertyContainer;

public class PropertyNotDefinedDefect implements Defect {

    private PFListEntity pfListEntity;
    private PFPropertyEntity pfPropertyEntity;
    private PropertyContainer propertyContainer;

    public PropertyNotDefinedDefect(PFListEntity pfListEntity, PFPropertyEntity pfPropertyEntity,
        PropertyContainer propertyContainer) {
        this.pfListEntity = pfListEntity;
        this.pfPropertyEntity = pfPropertyEntity;
        this.propertyContainer = propertyContainer;
    }

    public String getDefectMessage() {
        return "Property [" + pfPropertyEntity.getId() + "] which is defined in [" + pfListEntity.getFile().getPath()
                + "] is not set in [" + propertyContainer.getPropertyLoadedFrom() + "].";
    }

}
