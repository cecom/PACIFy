/*-
 * ========================LICENSE_START=================================
 * com.geewhiz.pacify.impl
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
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.geewhiz.pacify.defect.Defect;
import com.geewhiz.pacify.managers.EntityManager;
import com.geewhiz.pacify.managers.PropertyResolveManager;
import com.geewhiz.pacify.property.resolver.HashMapPropertyResolver;
import com.geewhiz.pacify.resolver.PropertyResolver;
import com.geewhiz.pacify.test.TestUtil;
import com.geewhiz.pacify.utils.LoggingUtils;

import static org.hamcrest.Matchers.*;



public class Features extends TestBase {

    @Before
    public void before() {
        Logger logger = LogManager.getLogger();
        LoggingUtils.setLogLevel(logger, Level.ERROR);
    }

    @Test
    public void ModifyDateFeature() {
        File testResourceFolder = new File("src/test/resources/2_Features/ModifyDate");
        File targetResourceFolder = new File("target/test-resources/2_Features/ModifyDate");

        LinkedHashSet<Defect> defects = createPrepareAndExecute(testResourceFolder, targetResourceFolder);

        Assert.assertEquals("We shouldnt get any defects.", 0, defects.size());
        Assert.assertEquals("Time of the readme.txt which is not replaced should be same.",
                (new File(targetResourceFolder, "package/readme.txt")).lastModified(), (new File(testResourceFolder, "package/readme.txt")).lastModified());

        Assert.assertThat("Timestamp", (new File(targetResourceFolder, "package/conf.txt")).lastModified(),
                greaterThan((new File(testResourceFolder, "package/conf.txt")).lastModified()));
    }

    private LinkedHashSet<Defect> createPrepareAndExecute(File testResourceFolder, File targetResourceFolder) {
        File packagePath = new File(targetResourceFolder, "package");

        TestUtil.removeOldTestResourcesAndCopyAgain(testResourceFolder, targetResourceFolder);

        PropertyResolveManager propertyResolveManager = createPropertyResolveManager();
        EntityManager entityManager = new EntityManager(packagePath);

        LinkedHashSet<Defect> defects = entityManager.initialize();

        // execute validation
        defects.addAll(createValidator(propertyResolveManager, packagePath).validateInternal(entityManager));

        // execute replacer
        defects.addAll(createReplacer(propertyResolveManager, packagePath).doReplacement(entityManager));

        return defects;
    }

    private PropertyResolveManager createPropertyResolveManager() {
        HashMapPropertyResolver hpr = new HashMapPropertyResolver();
        hpr.addProperty("foobar", "foobarValue");

        Set<PropertyResolver> propertyResolverList = new TreeSet<PropertyResolver>();
        propertyResolverList.add(hpr);
        PropertyResolveManager prm = new PropertyResolveManager(propertyResolverList);
        return prm;
    }

    private Replacer createReplacer(PropertyResolveManager propertyResolveManager, File packagePath) {
        Replacer replacer = new Replacer(propertyResolveManager);

        replacer.setPackagePath(packagePath);

        return replacer;
    }

    private Validator createValidator(PropertyResolveManager propertyResolveManager, File packagePath) {
        Validator validator = new Validator(propertyResolveManager);

        validator.setPackagePath(packagePath);
        validator.enableMarkerFileChecks();
        validator.enablePropertyResolveChecks();

        return validator;
    }
}
