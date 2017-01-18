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

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Assert;
import org.junit.Test;

import com.geewhiz.pacify.defect.Defect;
import com.geewhiz.pacify.defect.NotReplacedPropertyDefect;
import com.geewhiz.pacify.defect.PropertyNotDefinedInResolverDefect;
import com.geewhiz.pacify.managers.EntityManager;
import com.geewhiz.pacify.managers.PropertyResolveManager;
import com.geewhiz.pacify.property.resolver.fileresolver.FilePropertyResolver;
import com.geewhiz.pacify.resolver.PropertyResolver;
import com.geewhiz.pacify.test.TestUtil;
import com.geewhiz.pacify.utils.FileUtils;

public class TestMissingProperty extends TestBase {

    @Test
    public void checkForNotCorrect() {
        File testResourceFolder = new File("src/test/resources/testMissingProperty");
        File targetResourceFolder = new File("target/test-resources/testMissingProperty");

        TestUtil.removeOldTestResourcesAndCopyAgain(testResourceFolder, targetResourceFolder);

        File myTestProperty = new File(targetResourceFolder, "properties/example.properties");
        URL myTestPropertyURL = FileUtils.getFileUrl(myTestProperty);

        Assert.assertTrue("StartPath [" + targetResourceFolder.getPath() + "] doesn't exist!", targetResourceFolder.exists());

        PropertyResolveManager propertyResolveManager = createPropertyResolveManager(myTestPropertyURL);

        // todo: testbase nutzen vom erstellen
        Replacer replacer = new Replacer(propertyResolveManager);
        replacer.setPackagePath(targetResourceFolder);

        LinkedHashSet<Defect> result = replacer.doReplacement();

        List<Defect> defects = new ArrayList<Defect>(result);

        Assert.assertEquals(PropertyNotDefinedInResolverDefect.class, defects.get(0).getClass());
        Assert.assertEquals(2, defects.size());
        Assert.assertEquals("foobar5", ((PropertyNotDefinedInResolverDefect) defects.get(0)).getPProperty().getName());

        Assert.assertEquals(NotReplacedPropertyDefect.class, defects.get(1).getClass());
        Assert.assertEquals("foobar5", ((NotReplacedPropertyDefect) defects.get(1)).getPropertyId());

    }

    private PropertyResolveManager createPropertyResolveManager(URL myTestPropertyURL) {
        Set<PropertyResolver> resolverList = new TreeSet<PropertyResolver>();
        FilePropertyResolver filePropertyResolver = new FilePropertyResolver(myTestPropertyURL);
        resolverList.add(filePropertyResolver);

        PropertyResolveManager propertyResolveManager = new PropertyResolveManager(resolverList);
        return propertyResolveManager;
    }
}
