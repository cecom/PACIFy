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
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Assert;
import org.junit.Test;

import com.geewhiz.pacify.managers.PropertyResolveManager;
import com.geewhiz.pacify.property.resolver.cmdresolver.CmdPropertyResolver;
import com.geewhiz.pacify.resolver.PropertyResolver;

public class TestCmdResolver {

    @Test
    public void testAll() {
        File testBasePath = new File("target/test-classes/TestCmdResolver");
        File myPackagePath = new File(testBasePath, "package");
        File myResultPath = new File(testBasePath, "result");

        Assert.assertTrue("TestBasePath [" + testBasePath.getPath() + "] doesn't exist!", testBasePath.exists());

        PropertyResolveManager propertyResolveManager = getPropertyResolveManager();

        Replacer replacer = new Replacer(propertyResolveManager);
        replacer.setPackagePath(myPackagePath);
        replacer.setCreateCopy(Boolean.FALSE);
        replacer.execute();

        TestUtil.checkIfResultIsAsExpected(myPackagePath, myResultPath);
    }

    private PropertyResolveManager getPropertyResolveManager() {
        Properties properties = new Properties();
        properties.put("foobar3", "%{foobar1}:%{foobar2}");
        properties.put("foobar2", "6299äÖ9");
        properties.put("foobar5", "%{foobar6}");
        properties.put("foobar6", "%{foobar7}");
        properties.put("foobar7", "someProperty");
        properties.put("path", "d:\\tmp\\somefolder");
        properties.put("foobar1", "http://0815");
        properties.put("foobar4", "%{foobar2}/%{foobar1}/%{foobar5}");

        CmdPropertyResolver cmdPropertyResolver = new CmdPropertyResolver(properties);

        Set<PropertyResolver> resolverList = new TreeSet<PropertyResolver>();
        resolverList.add(cmdPropertyResolver);

        PropertyResolveManager propertyResolveManager = new PropertyResolveManager(resolverList);
        return propertyResolveManager;
    }
}
