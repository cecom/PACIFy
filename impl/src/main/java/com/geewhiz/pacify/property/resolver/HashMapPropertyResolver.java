/*-
 * ========================LICENSE_START=================================
 * com.geewhiz.pacify.impl
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

package com.geewhiz.pacify.property.resolver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.geewhiz.pacify.defect.Defect;



public class HashMapPropertyResolver extends BasePropertyResolver {

    Map<String, String> properties          = new HashMap<String, String>();
    List<String>        protectedProperties = new ArrayList<String>();

    public void addProperty(String key, String value) {
        if (key.startsWith("*")) {
            key = key.substring(1);
            protectedProperties.add(key);
        }
        properties.put(key, value);
    }

    @Override
    public boolean containsProperty(String key) {
        return properties.containsKey(key);
    }

    @Override
    public String getPropertyValue(String key) {
        return properties.get(key);
    }

    @Override
    public Set<String> getPropertyKeys() {
        return properties.keySet();
    }

    @Override
    public String getEncoding() {
        return "UTF-8";
    }

    @Override
    public String getPropertyResolverDescription() {
        return HashMapPropertyResolver.class.getSimpleName();
    }

    @Override
    public LinkedHashSet<Defect> checkForDuplicateEntry() {
        return new LinkedHashSet<Defect>();
    }

    @Override
    public String getBeginToken() {
        return "%{";
    }

    @Override
    public String getEndToken() {
        return "}";
    }

    @Override
    public boolean isProtectedProperty(String key) {
        return protectedProperties.contains(key);
    }

}
