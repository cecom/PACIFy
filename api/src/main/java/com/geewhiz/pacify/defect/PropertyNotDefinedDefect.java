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

import com.geewhiz.pacify.model.PMarker;
import com.geewhiz.pacify.model.PProperty;

public class PropertyNotDefinedDefect implements Defect {

    private PMarker   pMarker;
    private PProperty pproperty;
    private String    resolvers;

    public PropertyNotDefinedDefect(PMarker pMarker, PProperty pproperty,
        String resolvers) {
        this.pMarker = pMarker;
        this.pproperty = pproperty;
        this.resolvers = resolvers;
    }

    public String getDefectMessage() {
        return String
                .format("PropertyNotDefined:\n\t[MarkerFile=%s] \n\t[Property=%s] \n\t[resolvers=%s]",
                        pMarker.getFile().getAbsolutePath(),
                        pproperty.getName(), resolvers);
    }

}
