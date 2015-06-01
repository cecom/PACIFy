package com.geewhiz.pacify.checks.impl;

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

import java.util.ArrayList;
import java.util.List;

import com.geewhiz.pacify.checks.PMarkerCheck;
import com.geewhiz.pacify.defect.Defect;
import com.geewhiz.pacify.defect.PropertyHasCycleDefect;
import com.geewhiz.pacify.defect.PropertyNotDefinedDefect;
import com.geewhiz.pacify.defect.PropertyResolveDefect;
import com.geewhiz.pacify.exceptions.CycleDetectRuntimeException;
import com.geewhiz.pacify.exceptions.PropertyNotFoundException;
import com.geewhiz.pacify.exceptions.PropertyResolveException;
import com.geewhiz.pacify.managers.PropertyResolveManager;
import com.geewhiz.pacify.model.PMarker;
import com.geewhiz.pacify.model.PProperty;

public class CheckPropertyExists implements PMarkerCheck {

    private PropertyResolveManager propertyResolveManager;

    public CheckPropertyExists(PropertyResolveManager propertyResolveManager) {
        this.propertyResolveManager = propertyResolveManager;
    }

    public List<Defect> checkForErrors(PMarker pMarker) {
        List<Defect> defects = new ArrayList<Defect>();

        List<PProperty> pProperties = pMarker.getProperties();
        for (PProperty pProperty : pProperties) {
            if (propertyResolveManager.containsProperty(pProperty.getName())) {
                try {
                    propertyResolveManager.getPropertyValue(pProperty.getName());
                } catch (CycleDetectRuntimeException ce) {
                    defects.add(new PropertyHasCycleDefect(pMarker, ce.getProperty(), ce.getCycle()));
                } catch (PropertyResolveException re) {
                    defects.add(new PropertyResolveDefect(pMarker, pProperty, re.getResolvePath(), propertyResolveManager.toString()));
                }
                continue;
            }
            Defect defect = new PropertyNotDefinedDefect(pMarker, pProperty, propertyResolveManager.toString());
            defects.add(defect);
        }

        return defects;
    }
}
