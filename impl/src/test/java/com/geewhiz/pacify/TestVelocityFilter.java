package com.geewhiz.pacify;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Assert;
import org.junit.Test;

import com.geewhiz.pacify.defect.Defect;
import com.geewhiz.pacify.defect.WrongTokenDefinedDefect;
import com.geewhiz.pacify.managers.EntityManager;
import com.geewhiz.pacify.managers.PropertyResolveManager;
import com.geewhiz.pacify.property.resolver.HashMapPropertyResolver;
import com.geewhiz.pacify.resolver.PropertyResolver;
import com.geewhiz.pacify.test.TestUtil;

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

public class TestVelocityFilter {

    @Test
    public void testWrongToken() throws Exception {
        File testResourceFolder = new File("src/test/resources/testVelocityFilter/wrong/wrongToken");
        File targetResourceFolder = new File("target/test-resources/testVelocityFilter/wrong/wrongToken");

        TestUtil.removeOldTestResourcesAndCopyAgain(testResourceFolder, targetResourceFolder);

        File packagePath = new File(targetResourceFolder, "package");

        HashMapPropertyResolver hpr = new HashMapPropertyResolver();
        PropertyResolveManager prm = getPropertyResolveManager(hpr);

        Replacer replacer = new Replacer(prm);
        replacer.setPackagePath(packagePath);

        EntityManager entityManager = new EntityManager(packagePath);

        LinkedHashSet<Defect> result = entityManager.initialize();
        result.addAll(replacer.doReplacement(entityManager));

        List<Defect> defects = new ArrayList<Defect>(result);

        Assert.assertEquals(1, defects.size());
        Assert.assertEquals(WrongTokenDefinedDefect.class.getName(),
                defects.get(0).getClass().getName());
    }

    @Test
    public void testNotReplacedProperty() throws Exception {
        File testResourceFolder = new File("src/test/resources/testVelocityFilter/wrong/notReplacedProperty");
        File targetResourceFolder = new File("target/test-resources/testVelocityFilter/wrong/notReplacedProperty");

        TestUtil.removeOldTestResourcesAndCopyAgain(testResourceFolder, targetResourceFolder);

        File packagePath = new File(targetResourceFolder, "package");

        HashMapPropertyResolver spr = new HashMapPropertyResolver();
        PropertyResolveManager prm = getPropertyResolveManager(spr);

        Replacer replacer = new Replacer(prm);
        replacer.setPackagePath(packagePath);

        EntityManager entityManager = new EntityManager(packagePath);

        LinkedHashSet<Defect> result = entityManager.initialize();
        result.addAll(replacer.doReplacement(entityManager));

        List<Defect> defects = new ArrayList<Defect>(result);
        Assert.assertEquals(1, defects.size());
        Assert.assertEquals("com.geewhiz.pacify.defect.NotReplacedPropertyDefect", defects.get(0).getClass().getName());
    }

    @Test
    public void testSimpleReplacement() throws Exception {
        File testResourceFolder = new File("src/test/resources/testVelocityFilter/correct/simple");
        File targetResourceFolder = new File("target/test-resources/testVelocityFilter/correct/simple");

        TestUtil.removeOldTestResourcesAndCopyAgain(testResourceFolder, targetResourceFolder);

        File packagePath = new File(targetResourceFolder, "package");
        File expectedResultPath = new File(targetResourceFolder, "expectedResult");

        HashMapPropertyResolver spr = new HashMapPropertyResolver();

        PropertyResolveManager prm = getPropertyResolveManager(spr);

        Replacer replacer = new Replacer(prm);
        EntityManager entityManager = new EntityManager(packagePath);

        replacer.setPackagePath(packagePath);
        LinkedHashSet<Defect> defects = entityManager.initialize();
        defects.addAll(replacer.doReplacement(entityManager));

        Assert.assertEquals(0, defects.size());
        TestUtil.checkIfResultIsAsExpected(packagePath, expectedResultPath);
    }

    @Test
    public void testWithIfReplacement() throws Exception {
        File testResourceFolder = new File("src/test/resources/testVelocityFilter/correct/ifCondition");
        File targetResourceFolder = new File("target/test-resources/testVelocityFilter/correct/ifCondition");

        TestUtil.removeOldTestResourcesAndCopyAgain(testResourceFolder, targetResourceFolder);

        File packagePath = new File(targetResourceFolder, "package");
        File expectedResultPath = new File(targetResourceFolder, "expectedResult");

        HashMapPropertyResolver spr = new HashMapPropertyResolver();
        spr.addProperty("use.jdbc", "true");
        PropertyResolveManager prm = getPropertyResolveManager(spr);

        Replacer replacer = new Replacer(prm);
        EntityManager entityManager = new EntityManager(packagePath);

        replacer.setPackagePath(packagePath);
        LinkedHashSet<Defect> defects = entityManager.initialize();
        defects.addAll(replacer.doReplacement(entityManager));

        Assert.assertEquals(0, defects.size());
        TestUtil.checkIfResultIsAsExpected(packagePath, expectedResultPath);
    }

    @Test
    public void testWithIfElseReplacement() throws Exception {
        File testResourceFolder = new File("src/test/resources/testVelocityFilter/correct/ifElseCondition");
        File targetResourceFolder = new File("target/test-resources/testVelocityFilter/correct/ifElseCondition");

        TestUtil.removeOldTestResourcesAndCopyAgain(testResourceFolder, targetResourceFolder);

        File packagePath = new File(targetResourceFolder, "package");
        File expectedResultPath = new File(targetResourceFolder, "expectedResult");

        HashMapPropertyResolver spr = new HashMapPropertyResolver();
        spr.addProperty("use.jdbc", "false");
        PropertyResolveManager prm = getPropertyResolveManager(spr);

        Replacer replacer = new Replacer(prm);
        EntityManager entityManager = new EntityManager(packagePath);

        replacer.setPackagePath(packagePath);
        LinkedHashSet<Defect> defects = entityManager.initialize();
        defects.addAll(replacer.doReplacement(entityManager));

        Assert.assertEquals(0, defects.size());
        TestUtil.checkIfResultIsAsExpected(packagePath, expectedResultPath);
    }

    @Test
    public void testForEach() throws Exception {
        File testResourceFolder = new File("src/test/resources/testVelocityFilter/correct/forEachCondition");
        File targetResourceFolder = new File("target/test-resources/testVelocityFilter/correct/forEachCondition");

        TestUtil.removeOldTestResourcesAndCopyAgain(testResourceFolder, targetResourceFolder);

        File packagePath = new File(targetResourceFolder, "package");
        File expectedResultPath = new File(targetResourceFolder, "expectedResult");

        HashMapPropertyResolver spr = new HashMapPropertyResolver();
        spr.addProperty("a.list", "1,2,3,foo,bar");
        PropertyResolveManager prm = getPropertyResolveManager(spr);

        Replacer replacer = new Replacer(prm);
        EntityManager entityManager = new EntityManager(packagePath);

        replacer.setPackagePath(packagePath);
        LinkedHashSet<Defect> defects = entityManager.initialize();
        defects.addAll(replacer.doReplacement(entityManager));

        Assert.assertEquals(0, defects.size());
        TestUtil.checkIfResultIsAsExpected(packagePath, expectedResultPath);
    }

    private PropertyResolveManager getPropertyResolveManager(HashMapPropertyResolver hpr) {
        hpr.addProperty("foobar1", "foobar1Value");
        hpr.addProperty("foobar2", "foobar2Value");
        hpr.addProperty("jdbc.host.url", "123.123.123.133");
        hpr.addProperty("jdbc.host.port", "1234");

        Set<PropertyResolver> propertyResolverList = new TreeSet<PropertyResolver>();
        propertyResolverList.add(hpr);
        PropertyResolveManager prm = new PropertyResolveManager(propertyResolverList);
        return prm;
    }
}
