package com.geewhiz.pacify.property.resolver.cmdresolver;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.geewhiz.pacify.defect.Defect;
import com.geewhiz.pacify.resolver.PropertyResolver;
import com.geewhiz.pacify.resolver.PropertyResolverModule;
import com.google.inject.Provides;
import com.google.inject.multibindings.Multibinder;

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

public class CmdPropertyResolverModule extends PropertyResolverModule {

    Map<String, String> parameters;

    @Override
    public String getResolverId() {
        return "CmdResolver";
    }

    @Override
    protected void configure() {
        Multibinder<PropertyResolver> resolveBinder = Multibinder.newSetBinder(binder(), PropertyResolver.class);
        resolveBinder.addBinding().to(CmdPropertyResolver.class);
    }

    @Override
    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    @Provides
    public CmdPropertyResolver createFilePropertyResolver() {
        Properties properties = new Properties();
        properties.putAll(parameters);
        return new CmdPropertyResolver(properties);
    }

    @Override
    public List<Defect> getDefects() {
        return Collections.emptyList();
    }
}
