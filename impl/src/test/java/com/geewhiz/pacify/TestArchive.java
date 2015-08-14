package com.geewhiz.pacify;

import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

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

public class TestArchive {

    @BeforeClass
    public static void removeOldData() {
        TestUtil.removeOldTestResourcesAndCopyAgain();
    }

    @Test
    public void checkJar() {
        File testStartPath = new File("target/test-classes/testArchive/correct/jar/package");

        HashMapPropertyResolver hpr = new HashMapPropertyResolver();
        PropertyResolveManager prm = getPropertyResolveManager(hpr);

        Replacer replacer = new Replacer(prm);
        EntityManager entityManager = new EntityManager(testStartPath);

        replacer.setPackagePath(testStartPath);
        List<Defect> defects = entityManager.initialize();
        defects.addAll(replacer.doReplacement(entityManager));
    }

    @Test
    public void checkTar() {
        File testStartPath = new File("target/test-classes/testArchive/correct/tar/package");

        HashMapPropertyResolver hpr = new HashMapPropertyResolver();
        PropertyResolveManager prm = getPropertyResolveManager(hpr);

        Replacer replacer = new Replacer(prm);
        EntityManager entityManager = new EntityManager(testStartPath);

        replacer.setPackagePath(testStartPath);
        List<Defect> defects = entityManager.initialize();
        defects.addAll(replacer.doReplacement(entityManager));
    }

    @Test
    public void checkZip() {
        File testStartPath = new File("target/test-classes/testArchive/correct/zip/package");

        HashMapPropertyResolver hpr = new HashMapPropertyResolver();
        PropertyResolveManager prm = getPropertyResolveManager(hpr);

        Replacer replacer = new Replacer(prm);
        EntityManager entityManager = new EntityManager(testStartPath);

        replacer.setPackagePath(testStartPath);
        List<Defect> defects = entityManager.initialize();
        defects.addAll(replacer.doReplacement(entityManager));
    }

    private PropertyResolveManager getPropertyResolveManager(HashMapPropertyResolver hpr) {
        hpr.addProperty("foobar1", "foobar1Value");
        hpr.addProperty("foobar2", "foobar2Value");

        Set<PropertyResolver> propertyResolverList = new TreeSet<PropertyResolver>();
        propertyResolverList.add(hpr);
        PropertyResolveManager prm = new PropertyResolveManager(propertyResolverList);
        return prm;
    }

}
