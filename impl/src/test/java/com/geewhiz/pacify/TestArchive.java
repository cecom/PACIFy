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

import javax.xml.bind.JAXBException;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.junit.Assert;
import org.junit.Test;

import com.geewhiz.pacify.checks.impl.CheckCorrectArchiveType;
import com.geewhiz.pacify.defect.ArchiveDuplicateDefinedInPMarkerDefect;
import com.geewhiz.pacify.defect.ArchiveTypeNotImplementedDefect;
import com.geewhiz.pacify.defect.Defect;
import com.geewhiz.pacify.defect.FileDoesNotExistDefect;
import com.geewhiz.pacify.defect.FilterNotFoundDefect;
import com.geewhiz.pacify.defect.NoPlaceholderInTargetFileDefect;
import com.geewhiz.pacify.defect.NotReplacedPropertyDefect;
import com.geewhiz.pacify.defect.PropertyDuplicateDefinedInPMarkerDefect;
import com.geewhiz.pacify.managers.EntityManager;
import com.geewhiz.pacify.managers.PropertyResolveManager;
import com.geewhiz.pacify.model.PMarker;
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

public class TestArchive {

    @Test
    public void checkJar() throws ArchiveException, IOException {
        File testResourceFolder = new File("src/test/resources/testArchive/correct/jar");
        File targetResourceFolder = new File("target/test-resources/testArchive/correct/jar");

        List<Defect> defects = createPrepareAndExecutePacify(testResourceFolder, targetResourceFolder);

        Assert.assertEquals("We shouldnt get any defects.", 0, defects.size());

        File expectedArchive = new File(targetResourceFolder, "expectedResult/archive.jar");
        File outputArchive = new File(targetResourceFolder, "package/archive.jar");

        checkResultIsAsExpected(outputArchive, expectedArchive);
        Assert.assertArrayEquals("There should be no additional File", expectedArchive.getParentFile().list(), outputArchive.getParentFile().list());
    }

    @Test
    public void checkTar() throws ArchiveException, IOException {
        File testResourceFolder = new File("src/test/resources/testArchive/correct/tar");
        File targetResourceFolder = new File("target/test-resources/testArchive/correct/tar");

        List<Defect> defects = createPrepareAndExecutePacify(testResourceFolder, targetResourceFolder);

        Assert.assertEquals("We shouldnt get any defects.", 0, defects.size());

        File expectedArchive = new File(targetResourceFolder, "expectedResult/archive.tar");
        File outputArchive = new File(targetResourceFolder, "package/archive.tar");

        checkResultIsAsExpected(outputArchive, expectedArchive);
        Assert.assertArrayEquals("There should be no additional File", expectedArchive.getParentFile().list(), outputArchive.getParentFile().list());
    }

    @Test
    public void checkZip() throws ArchiveException, IOException {
        File testResourceFolder = new File("src/test/resources/testArchive/correct/zip");
        File targetResourceFolder = new File("target/test-resources/testArchive/correct/zip");

        List<Defect> defects = createPrepareAndExecutePacify(testResourceFolder, targetResourceFolder);

        Assert.assertEquals("We shouldnt get any defects.", 0, defects.size());

        File expectedArchive = new File(targetResourceFolder, "expectedResult/archive.zip");
        File outputArchive = new File(targetResourceFolder, "package/archive.zip");

        checkResultIsAsExpected(outputArchive, expectedArchive);
        Assert.assertArrayEquals("There should be no additional File", expectedArchive.getParentFile().list(), outputArchive.getParentFile().list());
    }

    @Test
    public void checkBigZip() throws ArchiveException, IOException {
        File testResourceFolder = new File("src/test/resources/testArchive/correct/bigZip");
        File targetResourceFolder = new File("target/test-resources/testArchive/correct/bigZip");

        List<Defect> defects = createPrepareAndExecutePacify(testResourceFolder, targetResourceFolder);

        Assert.assertEquals("We shouldnt get any defects.", 0, defects.size());

        File expectedArchive = new File(targetResourceFolder, "expectedResult/archive.zip");
        File outputArchive = new File(targetResourceFolder, "package/archive.zip");

        checkResultIsAsExpected(outputArchive, expectedArchive);
        Assert.assertArrayEquals("There should be no additional File", expectedArchive.getParentFile().list(), outputArchive.getParentFile().list());
    }

    @Test
    public void checkUnkownArchiveType() throws JAXBException {
        File source = new File("target/test-classes/testArchive/wrong/unkownArchiveType/package/wrong-CMFile.pacify");
        PMarker pMarker = TestUtil.readPMarker(source);

        CheckCorrectArchiveType checker = new CheckCorrectArchiveType();
        List<Defect> defects = checker.checkForErrors(pMarker);

        Assert.assertEquals("We should get a defect.", 1, defects.size());
        Assert.assertEquals("We expect ArchiveTypeNotImplementedDefect", ArchiveTypeNotImplementedDefect.class, defects.get(0).getClass());
    }

    @Test
    public void checkDuplicateArchiveEntry() {
        File packagePath = new File("target/test-classes/testArchive/wrong/duplicateEntry/package");

        List<Defect> defects = createPrepareAndExecuteValidator(packagePath);

        Assert.assertEquals("We should get a defect.", 1, defects.size());
        Assert.assertEquals("We expect ArchiveTypeNotImplementedDefect", ArchiveDuplicateDefinedInPMarkerDefect.class, defects.get(0).getClass());
    }

    @Test
    public void checkNotReplacedProperty() {
        File testResourceFolder = new File("target/test-classes/testArchive/wrong/notReplacedProperty");
        File targetResourceFolder = new File("target/test-resources/testArchive/wrong/notReplacedProperty");

        List<Defect> defects = createPrepareAndExecutePacify(testResourceFolder, targetResourceFolder);

        Assert.assertEquals("We should get a defect.", 2, defects.size());
        Assert.assertEquals("We expect NotReplacedPropertyDefect", NotReplacedPropertyDefect.class, defects.get(0).getClass());
        Assert.assertEquals("We expect NotReplacedPropertyDefect", NotReplacedPropertyDefect.class, defects.get(1).getClass());
        Assert.assertEquals("We expect missing property notReplacedProperty", "notReplacedProperty",
                ((NotReplacedPropertyDefect) defects.get(0)).getPropertyId());
        Assert.assertEquals("We expect missing property notReplacedProperty", "notReplacedProperty",
                ((NotReplacedPropertyDefect) defects.get(1)).getPropertyId());
    }

    @Test
    public void checkTargetFileDoesNotExist() {
        File packagePath = new File("target/test-classes/testArchive/wrong/targetFileDoesNotExist/package");

        List<Defect> defects = createPrepareAndExecuteValidator(packagePath);

        Assert.assertEquals("We should get a defect.", 1, defects.size());
        Assert.assertEquals("We expect FileDoesNotExistDefect", FileDoesNotExistDefect.class, defects.get(0).getClass());
    }

    @Test
    public void checkPlaceholderDoesNotExist() {
        File packagePath = new File("target/test-classes/testArchive/wrong/placeholderDoesNotExist/package");

        List<Defect> defects = createPrepareAndExecuteValidator(packagePath);

        Assert.assertEquals("We should get a defect.", 1, defects.size());
        Assert.assertEquals("We expect NoPlaceholderInTargetFileDefect", NoPlaceholderInTargetFileDefect.class, defects.get(0).getClass());
        Assert.assertEquals("We expect missingProperty", "missingProperty", ((NoPlaceholderInTargetFileDefect) defects.get(0)).getPProperty().getName());
    }

    @Test
    public void checkDuplicatePropertyEntry() {
        File packagePath = new File("target/test-classes/testArchive/wrong/duplicatePropertyEntry/package");

        List<Defect> defects = createPrepareAndExecuteValidator(packagePath);

        Assert.assertEquals("We should get a defect.", 1, defects.size());
        Assert.assertEquals("We expect PropertyDuplicateDefinedInPMarkerDefect", PropertyDuplicateDefinedInPMarkerDefect.class, defects.get(0).getClass());
        Assert.assertEquals("We expect missingProperty", "foobar2", ((PropertyDuplicateDefinedInPMarkerDefect) defects.get(0)).getPProperty().getName());
    }

    @Test
    public void checkWrongPacifyFilter() {
        File packagePath = new File("target/test-classes/testArchive/wrong/wrongPacifyFilter/package");

        List<Defect> defects = createPrepareAndExecuteValidator(packagePath);

        Assert.assertEquals("We should get a defect.", 1, defects.size());
        Assert.assertEquals("We expect FilterNotFoundDefect", FilterNotFoundDefect.class, defects.get(0).getClass());
        Assert.assertEquals("We expect missing.filter.class", "missing.filter.class", ((FilterNotFoundDefect) defects.get(0)).getPFile().getFilterClass());
    }

    private List<Defect> createPrepareAndExecuteValidator(File packagePath) {
        HashMapPropertyResolver hpr = new HashMapPropertyResolver();
        PropertyResolveManager prm = getPropertyResolveManager(hpr);

        EntityManager entityManager = new EntityManager(packagePath);
        Validator validator = new Validator(prm);
        validator.enableMarkerFileChecks();

        validator.setPackagePath(packagePath);

        List<Defect> defects = entityManager.initialize();
        defects.addAll(validator.validateInternal(entityManager));
        return defects;
    }

    private List<Defect> createPrepareAndExecutePacify(File testResourceFolder, File targetResourceFolder) {
        TestUtil.removeOldTestResourcesAndCopyAgain(testResourceFolder, targetResourceFolder);

        File packagePath = new File(targetResourceFolder, "package");

        HashMapPropertyResolver hpr = new HashMapPropertyResolver();
        PropertyResolveManager prm = getPropertyResolveManager(hpr);

        Replacer replacer = new Replacer(prm);
        EntityManager entityManager = new EntityManager(packagePath);

        replacer.setPackagePath(packagePath);
        List<Defect> defects = entityManager.initialize();
        defects.addAll(replacer.doReplacement(entityManager));
        return defects;
    }

    private PropertyResolveManager getPropertyResolveManager(HashMapPropertyResolver hpr) {
        hpr.addProperty("foobar1", "foobar1Value");
        hpr.addProperty("foobar2", "foobar2Value");

        Set<PropertyResolver> propertyResolverList = new TreeSet<PropertyResolver>();
        propertyResolverList.add(hpr);
        PropertyResolveManager prm = new PropertyResolveManager(propertyResolverList);
        return prm;
    }

    private void checkResultIsAsExpected(File replacedArchive, File expectedArchive) throws ArchiveException, IOException {
        archiveContainsEntries(replacedArchive, expectedArchive);
        archiveDoesNotContainAdditionEntries(replacedArchive, expectedArchive);
    }

    private void archiveContainsEntries(File replacedArchive, File expectedArchive) throws ArchiveException, IOException {
        ArchiveStreamFactory factory = new ArchiveStreamFactory();

        FileInputStream expectedIS = new FileInputStream(expectedArchive);
        ArchiveInputStream expectedAIS = factory.createArchiveInputStream(new BufferedInputStream(expectedIS));
        ArchiveEntry expectedEntry = null;
        while ((expectedEntry = expectedAIS.getNextEntry()) != null) {
            FileInputStream replacedIS = new FileInputStream(replacedArchive);
            ArchiveInputStream replacedAIS = factory.createArchiveInputStream(new BufferedInputStream(replacedIS));

            ArchiveEntry replacedEntry = null;
            boolean entryFound = false;
            while ((replacedEntry = replacedAIS.getNextEntry()) != null) {
                Assert.assertNotNull("We expect an entry.", replacedEntry);
                if (!expectedEntry.getName().equals(replacedEntry.getName())) {
                    continue;
                }
                entryFound = true;
                if (expectedEntry.isDirectory()) {
                    Assert.assertTrue("we expect a directory", replacedEntry.isDirectory());
                    break;
                }

                ByteArrayOutputStream expectedContent = readContent(expectedAIS);
                ByteArrayOutputStream replacedContent = readContent(replacedAIS);

                Assert.assertEquals("Content should be same of entry " + expectedEntry.getName(), expectedContent.toString("UTF-8"),
                        replacedContent.toString("UTF-8"));
                break;
            }

            replacedIS.close();
            Assert.assertTrue("Entry [" + expectedEntry.getName() + "] in the result archive expected.", entryFound);
        }

        expectedIS.close();
    }

    private void archiveDoesNotContainAdditionEntries(File replacedArchive, File expectedArchive) throws ArchiveException, IOException {
        ArchiveStreamFactory factory = new ArchiveStreamFactory();

        FileInputStream replacedIS = new FileInputStream(replacedArchive);
        ArchiveInputStream replacedAIS = factory.createArchiveInputStream(new BufferedInputStream(replacedIS));
        ArchiveEntry replacedEntry = null;
        while ((replacedEntry = replacedAIS.getNextEntry()) != null) {
            FileInputStream expectedIS = new FileInputStream(expectedArchive);
            ArchiveInputStream expectedAIS = factory.createArchiveInputStream(new BufferedInputStream(expectedIS));

            ArchiveEntry expectedEntry = null;
            boolean entryFound = false;
            while ((expectedEntry = expectedAIS.getNextEntry()) != null) {
                Assert.assertNotNull("We expect an entry.", expectedEntry);
                if (!replacedEntry.getName().equals(expectedEntry.getName())) {
                    continue;
                }
                entryFound = true;
                break;
            }

            expectedIS.close();
            Assert.assertTrue("Entry [" + replacedEntry.getName() + "] is not in the expected archive. This file shouldn't exist.", entryFound);
        }

        replacedIS.close();

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
