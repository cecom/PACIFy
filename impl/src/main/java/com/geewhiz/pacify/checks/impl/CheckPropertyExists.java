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

import java.util.LinkedHashSet;

import com.geewhiz.pacify.checks.PMarkerCheck;
import com.geewhiz.pacify.defect.Defect;
import com.geewhiz.pacify.defect.PropertyHasCycleDefect;
import com.geewhiz.pacify.defect.PropertyNotDefinedInResolverDefect;
import com.geewhiz.pacify.defect.ResolverDefect;
import com.geewhiz.pacify.exceptions.CycleDetectRuntimeException;
import com.geewhiz.pacify.exceptions.ResolverRuntimeException;
import com.geewhiz.pacify.managers.EntityManager;
import com.geewhiz.pacify.managers.PropertyResolveManager;
import com.geewhiz.pacify.model.PFile;
import com.geewhiz.pacify.model.PMarker;
import com.geewhiz.pacify.model.PProperty;

public class CheckPropertyExists implements PMarkerCheck {

    private PropertyResolveManager propertyResolveManager;

    public CheckPropertyExists(PropertyResolveManager propertyResolveManager) {
        this.propertyResolveManager = propertyResolveManager;
    }

    public LinkedHashSet<Defect> checkForErrors(EntityManager entityManager, PMarker pMarker) {
        LinkedHashSet<Defect> defects = new LinkedHashSet<Defect>();

        for (PFile pFile : entityManager.getPFilesFrom(pMarker)) {
            checkPFile(defects, pFile);
        }

        return defects;
    }

    private void checkPFile(LinkedHashSet<Defect> defects, PFile pFile) {
        for (PProperty pProperty : pFile.getPProperties()) {
            try {
                if (propertyResolveManager.containsProperty(pProperty.getName())) {
                    propertyResolveManager.getPropertyValue(pProperty);
                    continue;
                }
            } catch (CycleDetectRuntimeException ce) {
                defects.add(new PropertyHasCycleDefect(pProperty, ce.getProperty(), ce.getCycle()));
                continue;
            } catch (ResolverRuntimeException re) {
                defects.add(new ResolverDefect(pProperty, re.getResolver(), re.getMessage()));
                continue;
            }
            Defect defect = new PropertyNotDefinedInResolverDefect(pProperty, propertyResolveManager.toString());
            defects.add(defect);
        }
    }
}
