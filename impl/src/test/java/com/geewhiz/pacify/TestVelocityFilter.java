package com.geewhiz.pacify;

import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.geewhiz.pacify.defect.Defect;
import com.geewhiz.pacify.managers.EntityManager;
import com.geewhiz.pacify.managers.PropertyResolveManager;
import com.geewhiz.pacify.property.resolver.HashMapPropertyResolver;
import com.geewhiz.pacify.resolver.PropertyResolver;

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

    @BeforeClass
    public static void removeOldData() {
        TestUtil.removeOldTestResourcesAndCopyAgain();
    }

    @Test
    public void testWrongToken() throws Exception {
        File source = new File("target/test-classes/testVelocityFilter/wrong/wrongToken/package");

        HashMapPropertyResolver hpr = new HashMapPropertyResolver();
        PropertyResolveManager prm = getPropertyResolveManager(hpr);

        Replacer replacer = new Replacer(prm);
        EntityManager entityManager = new EntityManager(source);

        replacer.setPackagePath(source);
        List<Defect> defects = entityManager.initialize();
        defects.addAll(replacer.doReplacement(entityManager));
        // TODO:
        // Assert.assertEquals(1, defects.size());
        // Assert.assertEquals("com.geewhiz.pacify.defect.WrongTokenDefinedDefect",
        // defects.get(0).getClass().getName());
    }

    @Test
    public void testNotReplacedProperty() throws Exception {
        File source = new File("target/test-classes/testVelocityFilter/wrong/notReplacedProperty/package");

        HashMapPropertyResolver spr = new HashMapPropertyResolver();
        PropertyResolveManager prm = getPropertyResolveManager(spr);

        Replacer replacer = new Replacer(prm);
        EntityManager entityManager = new EntityManager(source);

        replacer.setPackagePath(source);
        List<Defect> defects = entityManager.initialize();
        defects.addAll(replacer.doReplacement(entityManager));

        Assert.assertEquals(1, defects.size());
        Assert.assertEquals("com.geewhiz.pacify.defect.NotReplacedPropertyDefect", defects.get(0).getClass().getName());
    }

    @Test
    public void testSimpleReplacement() throws Exception {
        File source = new File("target/test-classes/testVelocityFilter/correct/simple/package");

        HashMapPropertyResolver spr = new HashMapPropertyResolver();

        PropertyResolveManager prm = getPropertyResolveManager(spr);

        Replacer replacer = new Replacer(prm);
        EntityManager entityManager = new EntityManager(source);

        replacer.setPackagePath(source);
        List<Defect> defects = entityManager.initialize();
        defects.addAll(replacer.doReplacement(entityManager));

        Assert.assertEquals(0, defects.size());
        TestUtil.checkIfResultIsAsExpected(source, new File(source, "../result"));
    }

    @Test
    public void testWithIfReplacement() throws Exception {
        File source = new File("target/test-classes/testVelocityFilter/correct/ifCondition/package");

        HashMapPropertyResolver spr = new HashMapPropertyResolver();
        spr.addProperty("use.jdbc", "true");
        PropertyResolveManager prm = getPropertyResolveManager(spr);

        Replacer replacer = new Replacer(prm);
        EntityManager entityManager = new EntityManager(source);

        replacer.setPackagePath(source);
        List<Defect> defects = entityManager.initialize();
        defects.addAll(replacer.doReplacement(entityManager));

        Assert.assertEquals(0, defects.size());
        TestUtil.checkIfResultIsAsExpected(source, new File(source, "../result"));
    }

    @Test
    public void testWithIfElseReplacement() throws Exception {
        File source = new File("target/test-classes/testVelocityFilter/correct/ifElseCondition/package");

        HashMapPropertyResolver spr = new HashMapPropertyResolver();
        spr.addProperty("use.jdbc", "false");
        PropertyResolveManager prm = getPropertyResolveManager(spr);

        Replacer replacer = new Replacer(prm);
        EntityManager entityManager = new EntityManager(source);

        replacer.setPackagePath(source);
        List<Defect> defects = entityManager.initialize();
        defects.addAll(replacer.doReplacement(entityManager));

        Assert.assertEquals(0, defects.size());
        TestUtil.checkIfResultIsAsExpected(source, new File(source, "../result"));
    }

    @Test
    public void testForEach() throws Exception {
        File source = new File("target/test-classes/testVelocityFilter/correct/forEachCondition/package");

        HashMapPropertyResolver spr = new HashMapPropertyResolver();
        spr.addProperty("a.list", "1,2,3,foo,bar");
        PropertyResolveManager prm = getPropertyResolveManager(spr);

        Replacer replacer = new Replacer(prm);
        EntityManager entityManager = new EntityManager(source);

        replacer.setPackagePath(source);
        List<Defect> defects = entityManager.initialize();
        defects.addAll(replacer.doReplacement(entityManager));

        Assert.assertEquals(0, defects.size());
        TestUtil.checkIfResultIsAsExpected(source, new File(source, "../result"));
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
