package com.geewhiz.pacify;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
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

public class TestArchive {

    @BeforeClass
    public static void removeOldData() {
        TestUtil.removeOldTestResourcesAndCopyAgain();
    }

    @Test
    public void checkJar() throws ArchiveException, IOException {
        File basePath = new File("target/test-classes/testArchive/correct/jar");
        File packagePath = new File(basePath, "package");

        HashMapPropertyResolver hpr = new HashMapPropertyResolver();
        PropertyResolveManager prm = getPropertyResolveManager(hpr);

        Replacer replacer = new Replacer(prm);
        EntityManager entityManager = new EntityManager(packagePath);

        replacer.setPackagePath(packagePath);
        List<Defect> defects = entityManager.initialize();
        defects.addAll(replacer.doReplacement(entityManager));

        Assert.assertEquals("We shouldnt get any defects.", 0, defects.size());

        File expectedArchive = new File(basePath, "result/archive.jar");
        File outputArchive = new File(basePath, "package/archive.jar");

        resultIsAsExpected(outputArchive, expectedArchive);
    }

    @Test
    public void checkTar() throws ArchiveException, IOException {
        File basePath = new File("target/test-classes/testArchive/correct/tar");
        File packagePath = new File(basePath, "package");

        HashMapPropertyResolver hpr = new HashMapPropertyResolver();
        PropertyResolveManager prm = getPropertyResolveManager(hpr);

        Replacer replacer = new Replacer(prm);
        EntityManager entityManager = new EntityManager(packagePath);

        replacer.setPackagePath(packagePath);
        List<Defect> defects = entityManager.initialize();
        defects.addAll(replacer.doReplacement(entityManager));

        Assert.assertEquals("We shouldnt get any defects.", 0, defects.size());

        File expectedArchive = new File(basePath, "result/archive.tar");
        File outputArchive = new File(basePath, "package/archive.tar");

        resultIsAsExpected(outputArchive, expectedArchive);
    }

    @Test
    public void checkZip() throws ArchiveException, IOException {
        File basePath = new File("target/test-classes/testArchive/correct/zip");
        File packagePath = new File(basePath, "package");

        HashMapPropertyResolver hpr = new HashMapPropertyResolver();
        PropertyResolveManager prm = getPropertyResolveManager(hpr);

        Replacer replacer = new Replacer(prm);
        EntityManager entityManager = new EntityManager(packagePath);

        replacer.setPackagePath(packagePath);
        List<Defect> defects = entityManager.initialize();
        defects.addAll(replacer.doReplacement(entityManager));

        Assert.assertEquals("We shouldnt get any defects.", 0, defects.size());

        File expectedArchive = new File(basePath, "result/archive.zip");
        File outputArchive = new File(basePath, "package/archive.zip");

        resultIsAsExpected(outputArchive, expectedArchive);
    }

    private PropertyResolveManager getPropertyResolveManager(HashMapPropertyResolver hpr) {
        hpr.addProperty("foobar1", "foobar1Value");
        hpr.addProperty("foobar2", "foobar2Value");

        Set<PropertyResolver> propertyResolverList = new TreeSet<PropertyResolver>();
        propertyResolverList.add(hpr);
        PropertyResolveManager prm = new PropertyResolveManager(propertyResolverList);
        return prm;
    }

    private boolean resultIsAsExpected(File replacedArchive, File expectedArchive) throws ArchiveException, IOException {
        ArchiveStreamFactory factory = new ArchiveStreamFactory();

        FileInputStream replacedIS = new FileInputStream(replacedArchive);
        FileInputStream expectedIS = new FileInputStream(expectedArchive);

        ArchiveInputStream replacedAIS = factory.createArchiveInputStream(new BufferedInputStream(replacedIS));
        ArchiveInputStream expectedAIS = factory.createArchiveInputStream(new BufferedInputStream(expectedIS));

        ArchiveEntry expectedEntry = null;
        while ((expectedEntry = expectedAIS.getNextEntry()) != null) {
            ArchiveEntry replacedEntry = replacedAIS.getNextEntry();
            Assert.assertNotNull("We expect an entry.", replacedEntry);

            Assert.assertEquals(expectedEntry.getName(), replacedEntry.getName());

            if (expectedEntry.isDirectory()) {
                continue;
            }

            ByteArrayOutputStream expectedContent = readContent(expectedAIS);
            ByteArrayOutputStream replacedContent = readContent(replacedAIS);

            Assert.assertEquals(expectedContent.toString("UTF-8"), replacedContent.toString("UTF-8"));
        }

        replacedIS.close();
        expectedIS.close();

        return true;
    }

    private ByteArrayOutputStream readContent(ArchiveInputStream ais) throws IOException {
        byte[] content = new byte[2048];
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        BufferedOutputStream bos = new BufferedOutputStream(result);

        int len;
        while ((len = ais.read(content)) != -1)
        {
            bos.write(content, 0, len);
        }
        bos.close();
        content = null;

        return result;
    }
}
