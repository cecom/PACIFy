/*-
 * ========================LICENSE_START=================================
 * com.geewhiz.pacify.api
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
package com.geewhiz.pacify.resolver;

import java.util.LinkedHashSet;
import java.util.Map;

import com.geewhiz.pacify.defect.Defect;
import com.google.inject.AbstractModule;

/**
 * 
 * a marker class for ServiceLoader.
 * 
 */
public abstract class PropertyResolverModule extends AbstractModule {

    public abstract String getResolverId();

    public abstract void setParameters(Map<String, String> parameters);

    public abstract LinkedHashSet<Defect> getDefects();

}
