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

import com.geewhiz.pacify.property.PropertyResolveManager;
import com.geewhiz.pacify.property.resolver.fileresolver.FilePropertyResolver;
import com.geewhiz.pacify.resolver.PropertyResolver;

public class TestRecursivePropertyReplacement {

    @Test
    public void testAll() {
        File startPath = new File("target/test-classes/recursePropertyReplacement");
        File myTestProperty = new File(startPath, "myProperties.properties");

        Assert.assertTrue("StartPath [" + startPath.getPath() + "] doesn't exist!", startPath.exists());

        PropertyResolveManager propertyResolveManager = getPropertyResolveManager(myTestProperty);

        Replacer replacer = new Replacer(propertyResolveManager);
        replacer.setPackagePath(startPath);
        replacer.setCreateCopy(Boolean.FALSE);
        replacer.execute();

        TestUtil.checkIfResultIsAsExpected(startPath);
    }

    private PropertyResolveManager getPropertyResolveManager(File myTestProperty) {
        FilePropertyResolver filePropertyResolver = new FilePropertyResolver(TestUtil.getURLForFile(myTestProperty));

        Set<PropertyResolver> resolverList = new TreeSet<PropertyResolver>();
        resolverList.add(filePropertyResolver);

        PropertyResolveManager propertyResolveManager = new PropertyResolveManager(resolverList);
        return propertyResolveManager;
    }
}
