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

package com.geewhiz.pacify.managers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tools.ant.types.FilterSet;

import com.geewhiz.pacify.defect.Defect;
import com.geewhiz.pacify.exceptions.CycleDetectRuntimeException;
import com.geewhiz.pacify.model.PProperty;
import com.geewhiz.pacify.resolver.PropertyResolver;
import com.google.inject.Inject;

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

    public Set<String> getPropertyKeys() {
        Set<String> propertyKeys = new TreeSet<String>();
        for (PropertyResolver propertyResolver : propertyResolverList) {
            propertyKeys.addAll(propertyResolver.getPropertyKeys());
        }
        return propertyKeys;
    }

    public void resolveProperty(PProperty pProperty) {
        if (pProperty.isResolved()) {
            return;
        }

        String value = resolvePropertyWithCycleDetect(pProperty, new ArrayList<String>(), pProperty.getBeginToken(), pProperty.getEndToken());

        if (value == null) {
            logger.debug("value for [{}] could not be resolved.", pProperty.getName());
            return;
        }

        pProperty.setValue(value);
        pProperty.setIsResolved(true);

        boolean isProtected = isProtectedProperty(pProperty.getName());

        if (!pProperty.isConvertBackslashToSlash()) {
            logger.debug("             Resolved property [{}] to value [{}]", pProperty.getName(), isProtected ? "**********" : pProperty.getValue());
        } else {
            String convertedString = pProperty.getValue().replace('\\', '/');
            logger.debug("             Resolved property [{}] with original value [{}] to [{}] (backslash convertion)", pProperty.getName(),
                    isProtected ? "**********" : pProperty.getValue(), isProtected ? "**********" : convertedString);
            pProperty.setValue(convertedString);
        }
    }

    public boolean isProtectedProperty(String property) {
        for (PropertyResolver propertyResolver : propertyResolverList) {
            if (!propertyResolver.containsProperty(property)) {
                continue;
            }

            boolean isProtected = propertyResolver.isProtectedProperty(property);
            if (propertyResolver.propertyUsesToken(property)) {
                for (String reference : propertyResolver.getReferencedProperties(property)) {
                    isProtected |= isProtectedProperty(reference);
                }
            }

            return isProtected;
        }

        return false;
    }

    private String resolvePropertyWithCycleDetect(PProperty pProperty, List<String> propertyCycleDetector, String fileBeginToken, String fileEndToken) {
        for (PropertyResolver propertyResolver : propertyResolverList) {
            if (!propertyResolver.containsProperty(pProperty.getName())) {
                continue;
            }

            if (propertyResolver.propertyUsesToken(pProperty.getName())) {
                return replaceTokens(propertyResolver, pProperty, propertyCycleDetector, fileBeginToken, fileEndToken);
            }
            return propertyResolver.getPropertyValue(pProperty.getName());
        }

        return null;

        // todo: warum drin?
        // if (propertyCycleDetector.isEmpty()) {
        // }
        // throw new CycleDetectRuntimeException(property, StringUtils.join(propertyCycleDetector, "->") + "->" + property);
    }

    private String replaceTokens(PropertyResolver propertyResolver, PProperty pProperty, List<String> propertyCycleDetector, String fileBeginToken,
            String fileEndToken) {
        if (propertyCycleDetector.contains(pProperty.getName())) {
            String message = StringUtils.join(propertyCycleDetector, "->") + "->" + pProperty.getName();
            throw new CycleDetectRuntimeException(pProperty.getName(), message);
        }

        propertyCycleDetector.add(pProperty.getName());

        FilterSet filterSet = propertyResolver.createFilterSet();
        for (String reference : propertyResolver.getReferencedProperties(pProperty.getName())) {
            PProperty pReference = createReference(pProperty, reference);
            pProperty.addAReference(pReference);

            String value = resolvePropertyWithCycleDetect(pReference, propertyCycleDetector, fileBeginToken, fileEndToken);
            if (value != null) {
                filterSet.addFilter(reference, value);
                continue;
            }

            if (!fileBeginToken.equals(filterSet.getBeginToken()) || !fileEndToken.equals(filterSet.getEndToken())) {
                value = fileBeginToken + reference + fileEndToken;
                filterSet.addFilter(reference, value);
            }

        }

        String valueWithToken = propertyResolver.getPropertyValue(pProperty.getName());
        return filterSet.replaceTokens(valueWithToken);
    }

    private PProperty createReference(PProperty pProperty, String referenceName) {
        PProperty reference = (PProperty) pProperty.clone();
        reference.setName(referenceName);
        return reference;
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
        Collection<Defect> result = new LinkedHashSet<Defect>();
        for (PropertyResolver propertyResolver : propertyResolverList) {
            result.addAll(propertyResolver.checkForDuplicateEntry());
        }
        return result;
    }

};
