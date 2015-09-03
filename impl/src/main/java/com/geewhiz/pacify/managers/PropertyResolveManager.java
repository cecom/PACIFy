package com.geewhiz.pacify.managers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tools.ant.types.FilterSet;

import com.geewhiz.pacify.defect.Defect;
import com.geewhiz.pacify.exceptions.CycleDetectRuntimeException;
import com.geewhiz.pacify.exceptions.PropertyNotFoundRuntimeException;
import com.geewhiz.pacify.exceptions.PropertyResolveRuntimeException;
import com.geewhiz.pacify.model.PProperty;
import com.geewhiz.pacify.resolver.PropertyResolver;
import com.google.inject.Inject;

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

public class PropertyResolveManager {

    private Logger        logger = LogManager.getLogger(PropertyResolveManager.class.getName());

    Set<PropertyResolver> propertyResolverList;

    @Inject
    public PropertyResolveManager(Set<PropertyResolver> propertyResolverList) {
        this.propertyResolverList = propertyResolverList;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        Iterator<PropertyResolver> iter = propertyResolverList.iterator();
        while (iter.hasNext()) {
            sb.append("[");
            PropertyResolver propertyResolver = iter.next();
            sb.append(propertyResolver.getPropertyResolverDescription());
            sb.append("]");
            if (iter.hasNext()) {
                sb.append("|");
            }
        }
        return sb.toString();
    }

    public Map<String, String> getProperties() {

        Set<String> propertyKeys = new TreeSet<String>();
        for (PropertyResolver propertyResolver : propertyResolverList) {
            propertyKeys.addAll(propertyResolver.getPropertyKeys());
        }

        Map<String, String> result = new TreeMap<String, String>();
        for (String propertyKey : propertyKeys) {
            result.put(propertyKey, getPropertyValue(propertyKey, new ArrayList<String>()));
        }

        return result;
    }

    public String getPropertyValue(PProperty pProperty) {
        String property = pProperty.getName();
        String value = getPropertyValue(property, new ArrayList<String>());

        if (!pProperty.isConvertBackslashToSlash()) {
            logger.debug("       Resolved property [{}] to value [{}]", property, value);
            return value;
        }

        String convertedString = value.replace('\\', '/');
        logger.debug("       Resolved property [{}] with original value [{}] to [{}] (backslash convertion)", property, value, convertedString);

        return convertedString;
    }

    private String getPropertyValue(String property, List<String> propertyCycleDetector) {
        for (PropertyResolver propertyResolver : propertyResolverList) {
            if (!propertyResolver.containsProperty(property)) {
                continue;
            }

            if (propertyResolver.propertyUsesToken(property)) {
                return replaceTokens(propertyResolver, property, propertyCycleDetector);
            }
            return propertyResolver.getPropertyValue(property);
        }

        if (propertyCycleDetector.isEmpty()) {
            throw new PropertyNotFoundRuntimeException(property);
        }
        throw new PropertyResolveRuntimeException(property, StringUtils.join(propertyCycleDetector, "->") + "->" + property);
    }

    private String replaceTokens(PropertyResolver propertyResolver, String property, List<String> propertyCycleDetector) {
        if (propertyCycleDetector.contains(property)) {
            String message = StringUtils.join(propertyCycleDetector, "->") + "->" + property;
            throw new CycleDetectRuntimeException(property, message);
        }

        propertyCycleDetector.add(property);

        FilterSet filterSet = propertyResolver.createFilterSet();
        for (String reference : propertyResolver.getReferencedProperties(property)) {
            filterSet.addFilter(reference, getPropertyValue(reference, propertyCycleDetector));
        }

        String valueWithToken = propertyResolver.getPropertyValue(property);
        return filterSet.replaceTokens(valueWithToken);
    }

    public boolean containsProperty(String name) {
        for (PropertyResolver propertyResolver : propertyResolverList) {
            if (propertyResolver.containsProperty(name)) {
                return true;
            }
        }
        return false;
    }

    public Collection<Defect> checkForDuplicateEntry() {
        Collection<Defect> result = new ArrayList<Defect>();
        for (PropertyResolver propertyResolver : propertyResolverList) {
            result.addAll(propertyResolver.checkForDuplicateEntry());
        }
        return result;
    }
};
