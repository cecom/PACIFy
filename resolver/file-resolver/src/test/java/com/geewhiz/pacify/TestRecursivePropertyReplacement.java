package com.geewhiz.pacify;

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

import java.io.File;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Assert;
import org.junit.Test;

import com.geewhiz.pacify.managers.PropertyResolveManager;
import com.geewhiz.pacify.property.resolver.fileresolver.FilePropertyResolver;
import com.geewhiz.pacify.resolver.PropertyResolver;

public class TestRecursivePropertyReplacement {

    @Test
    public void testAll() {
        File testBasePath = new File("target/test-classes/recursePropertyReplacement");
        File myTestProperty = new File(testBasePath, "properties/myProperties.properties");
        File myPackagePath = new File(testBasePath, "package");
        File myResultPath = new File(testBasePath, "result");

        Assert.assertTrue("TestBasePath [" + testBasePath.getPath() + "] doesn't exist!", testBasePath.exists());

        PropertyResolveManager propertyResolveManager = getPropertyResolveManager(myTestProperty);

        Replacer replacer = new Replacer(propertyResolveManager);
        replacer.setPackagePath(myPackagePath);
        replacer.execute();

        TestUtil.checkIfResultIsAsExpected(myPackagePath, myResultPath);
    }

    private PropertyResolveManager getPropertyResolveManager(File myTestProperty) {
        FilePropertyResolver filePropertyResolver = new FilePropertyResolver(TestUtil.getURLForFile(myTestProperty));

        Set<PropertyResolver> resolverList = new TreeSet<PropertyResolver>();
        resolverList.add(filePropertyResolver);

        PropertyResolveManager propertyResolveManager = new PropertyResolveManager(resolverList);
        return propertyResolveManager;
    }
}
