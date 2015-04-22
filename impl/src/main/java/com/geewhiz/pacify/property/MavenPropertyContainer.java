package com.geewhiz.pacify.property;

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

import java.util.Collections;
import java.util.List;
import java.util.Properties;

import com.geewhiz.pacify.defect.Defect;
import com.geewhiz.pacify.resolver.PropertyResolver;

public class MavenPropertyContainer implements PropertyResolver {

    Properties properties;
    String encoding;

    public MavenPropertyContainer(Properties properties, String encoding) {
        this.properties = properties;
        this.encoding = encoding;
    }

    public boolean containsProperty(String key) {
        return getProperties().containsKey(key);
    }

    public String getPropertyValue(String key) {
        if (containsProperty(key)) {
            return getProperties().getProperty(key);
        }
        throw new IllegalArgumentException("Property [" + key + "] not defined within maven... Aborting!");
    }

    public Properties getProperties() {
        return properties;
    }

    public List<Defect> checkForDuplicateEntry() {
        return Collections.emptyList();
    }

    public String getPropertyResolverDescription() {
        return "maven";
    }

    public String getEncoding() {
        return encoding;
    }
}
