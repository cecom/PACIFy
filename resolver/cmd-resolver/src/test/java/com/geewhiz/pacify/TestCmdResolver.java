/*-
 * ========================LICENSE_START=================================
 * com.geewhiz.pacify.resolver.cmd-resolver
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

import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Assert;
import org.junit.Test;

import com.geewhiz.pacify.defect.Defect;
import com.geewhiz.pacify.managers.PropertyResolveManager;
import com.geewhiz.pacify.property.resolver.cmdresolver.CmdPropertyResolver;
import com.geewhiz.pacify.resolver.PropertyResolver;
import com.geewhiz.pacify.test.TestUtil;

public class TestCmdResolver extends TestBase {

    @Test
    public void testAll() {
        String testFolder = "TestCmdResolver";

        LinkedHashSet<Defect> defects = createPrepareValidateAndReplace(testFolder, createPropertyResolveManager());

        Assert.assertEquals("We shouldnt get any defects.", 0, defects.size());

        TestUtil.checkIfResultIsAsExpected(testFolder);
    }

    private PropertyResolveManager createPropertyResolveManager() {
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
