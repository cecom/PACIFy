package com.geewhiz.pacify.property.resolver.cmdresolver;

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

import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import com.geewhiz.pacify.defect.Defect;
import com.geewhiz.pacify.property.resolver.BasePropertyResolver;

public class CmdPropertyResolver extends BasePropertyResolver {

    private static final String END_TOKEN      = "endToken";
    private static final String BEGIN_TOKEN    = "beginToken";
    private static final String ENCODING_TOKEN = "encoding";
    private Properties          properties;

    public CmdPropertyResolver(Properties properties) {
        this.properties = properties;
    }

    @Override
    public boolean containsProperty(String key) {
        return properties.containsKey(key);
    }

    @Override
    public boolean isProtectedProperty(String key) {
        return false;
    }

    @Override
    public String getPropertyValue(String key) {
        return properties.getProperty(key);
    }

    @Override
    public LinkedHashSet<Defect> checkForDuplicateEntry() {
        return new LinkedHashSet<Defect>();
    }

    @Override
    public Set<String> getPropertyKeys() {
        Set<String> result = new TreeSet<String>();

        for (Enumeration<Object> enumerator = properties.keys(); enumerator.hasMoreElements();) {
            String property = (String) enumerator.nextElement();
            if (BEGIN_TOKEN.equals(property)) {
                continue;
            }
            if (END_TOKEN.equals(property)) {
                continue;
            }
            if (ENCODING_TOKEN.equals(property)) {
                continue;
            }
            result.add(property);
        }

        return result;
    }

    @Override
    public String getEncoding() {
        return properties.getProperty(ENCODING_TOKEN, "utf-8");
    }

    @Override
    public String getPropertyResolverDescription() {
        return "CommandLine";
    }

    public String getBeginToken() {
        return properties.getProperty(BEGIN_TOKEN, "%{");
    }

    public String getEndToken() {
        return properties.getProperty(END_TOKEN, "}");
    }

}
