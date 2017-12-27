/*-
 * ========================LICENSE_START=================================
 * com.geewhiz.pacify.resolver.file-resolver
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

package com.geewhiz.pacify;

import java.net.URL;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.geewhiz.pacify.property.resolver.fileresolver.FilePropertyResolver;

public class TestRecursivePropertyFileLoader {

    Properties allPropertiesShouldLookLike          = new Properties();
    Properties child1PropertiesShouldLookLike       = new Properties();
    Properties child2PropertiesShouldLookLike       = new Properties();
    Properties childOfChildPropertiesShouldLookLike = new Properties();
    Properties basePropertiesShouldLookLike         = new Properties();

    @Before
    public void setUp() throws Exception {
        basePropertiesShouldLookLike.put("env.name", "baseEnvName");
        basePropertiesShouldLookLike.put("SomeBaseProperty", "SomeBasePropertyValue");
        basePropertiesShouldLookLike.put("aPropertyWithSlash", "temp\\asdf");

        child1PropertiesShouldLookLike.put("env.name", "child1EnvName");
        child1PropertiesShouldLookLike.put("SomeChild1Property", "SomeChild1PropertyValue");

        child2PropertiesShouldLookLike.put("env.name", "child2EnvName");
        child2PropertiesShouldLookLike.put("SomeChild2Property", "SomeChild2PropertyValue");

        childOfChildPropertiesShouldLookLike.put("env.name", "ChildOfChildEnv");
        childOfChildPropertiesShouldLookLike.put("SomeChildOfChildProperty", "SomeChildOfChildPropertyValue");

        allPropertiesShouldLookLike.putAll(basePropertiesShouldLookLike);
        allPropertiesShouldLookLike.putAll(child1PropertiesShouldLookLike);
        allPropertiesShouldLookLike.putAll(child2PropertiesShouldLookLike);
        allPropertiesShouldLookLike.putAll(childOfChildPropertiesShouldLookLike);

    }

    @Test
    public void testWithClasspath() {
        URL url = this.getClass().getClassLoader().getResource("properties/subfolder/ChildOfChilds.properties");

        FilePropertyResolver filePropertyResolver = new FilePropertyResolver(url);

        Assert.assertEquals(allPropertiesShouldLookLike.size(), filePropertyResolver.getPropertyKeys().size());

        for (Object propertyKey : allPropertiesShouldLookLike.keySet()) {
            Assert.assertEquals(allPropertiesShouldLookLike.get(propertyKey), filePropertyResolver.getPropertyValue((String) propertyKey));
        }

        Assert.assertEquals(childOfChildPropertiesShouldLookLike, filePropertyResolver.getLocalProperties());
        Assert.assertEquals(child1PropertiesShouldLookLike, filePropertyResolver.getParents().get(0).getLocalProperties());
        Assert.assertEquals(child2PropertiesShouldLookLike, filePropertyResolver.getParents().get(1).getLocalProperties());
        Assert.assertEquals(basePropertiesShouldLookLike, filePropertyResolver.getParents().get(0).getParents().get(0).getLocalProperties());
    }
}
