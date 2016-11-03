package com.geewhiz.pacify.property.resolver;

import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.tools.ant.types.FilterSet;

import com.geewhiz.pacify.resolver.PropertyResolver;
import com.geewhiz.pacify.utils.RegExpUtils;

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

public abstract class BasePropertyResolver implements PropertyResolver {

    @Override
    public FilterSet createFilterSet() {
        FilterSet filterset = new FilterSet();

        filterset.setBeginToken(getBeginToken());
        filterset.setEndToken(getEndToken());

        return filterset;
    }

    @Override
    public boolean propertyUsesToken(String property) {
        return getMatcher(getPropertyValue(property)).find();
    }

    protected Matcher getMatcher(String propertyValue) {
        Pattern pattern = RegExpUtils.getDefaultPattern(getBeginToken(), getEndToken());

        Matcher matcher = pattern.matcher(propertyValue);
        return matcher;
    }

    public Set<String> getReferencedProperties(String property) {
        String propertyValue = getPropertyValue(property);
        Matcher matcher = getMatcher(propertyValue);

        Set<String> result = new TreeSet<String>();
        while (matcher.find()) {
            String propertyId = matcher.group(1);
            result.add(propertyId);
        }

        return result;
    }

    @Override
    public int compareTo(PropertyResolver o) {
        return getPropertyResolverDescription().compareTo(o.getPropertyResolverDescription());
    }

}
