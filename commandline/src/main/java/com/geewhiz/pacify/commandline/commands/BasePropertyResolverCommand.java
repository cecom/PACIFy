/*-
 * ========================LICENSE_START=================================
 * com.geewhiz.pacify.commandline
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

package com.geewhiz.pacify.commandline.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

import com.beust.jcommander.DynamicParameter;
import com.beust.jcommander.Parameter;
import com.geewhiz.pacify.resolver.PropertyResolverModule;



public abstract class BasePropertyResolverCommand {

    @Parameter(names = { "-r", "--resolvers" }, description = "Where to get the Property values from. Provided ones: FileResolver and CmdResolver (can be chained). Example --resolvers=CmdResolver,FileResolver", required = true)
    private String              resolvers;

    @DynamicParameter(names = "-R", description = "Properties which will be forwarded to the resolver")
    private Map<String, String> resolverParameter = new HashMap<String, String>();

    public List<PropertyResolverModule> getPropertyResolverModules() {
        List<PropertyResolverModule> result = new ArrayList<PropertyResolverModule>();
        for (String resolver : resolvers.split(",")) {
            result.add(getModuleForId(resolver));
        }
        return result;
    }

    private PropertyResolverModule getModuleForId(String resolver) {
        for (PropertyResolverModule module : ServiceLoader.load(PropertyResolverModule.class)) {
            if (module.getResolverId().equals(resolver)) {
                module.setParameters(getModuleProperties(module));
                return module;
            }
        }
        throw new IllegalArgumentException("Resolver [" + resolver + "] not found!");
    }

    private Map<String, String> getModuleProperties(PropertyResolverModule module) {
        Map<String, String> result = new HashMap<String, String>();

        String propertyResolverId = module.getResolverId();
        for (Map.Entry<String, String> entry : resolverParameter.entrySet()) {
            String key = entry.getKey();

            if (!key.contains(".")) {
                continue;
            }

            int idx = key.indexOf(".");

            if (!propertyResolverId.equals(key.substring(0, idx))) {
                continue;
            }

            result.put(key.substring(idx + 1), entry.getValue());
        }

        return result;
    }

}
