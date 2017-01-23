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
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.jar.JarInputStream;

import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.geewhiz.pacify.defect.ArchiveDuplicateDefinedInPMarkerDefect;
import com.geewhiz.pacify.defect.ArchiveTypeNotImplementedDefect;
import com.geewhiz.pacify.defect.Defect;
import com.geewhiz.pacify.defect.FileDoesNotExistDefect;
import com.geewhiz.pacify.defect.FilterNotFoundDefect;
import com.geewhiz.pacify.defect.NoPlaceholderInTargetFileDefect;
import com.geewhiz.pacify.defect.NotReplacedPropertyDefect;
import com.geewhiz.pacify.defect.PlaceholderNotDefinedDefect;
import com.geewhiz.pacify.defect.PropertyDuplicateDefinedInPMarkerDefect;
import com.geewhiz.pacify.defect.PropertyNotDefinedInResolverDefect;
import com.geewhiz.pacify.utils.LoggingUtils;

public class TestArchive extends TestBase {

    Map<String, String> propertiesToUseWhileResolving = new HashMap<String, String>();
    Logger              logger                        = LogManager.getLogger(TestArchive.class.getName());

    @Before
    public void before() {
        LoggingUtils.setLogLevel(logger, Level.INFO);

        propertiesToUseWhileResolving.put("foobar1", "foobar1Value");
        propertiesToUseWhileResolving.put("foobar2", "foobar2Value");
    }

    @Test
    public void checkJar() throws ArchiveException, IOException {
        String testFolder = "testArchive/correct/jar";

        File targetResourceFolder = new File("target/test-resources/", testFolder);

        LinkedHashSet<Defect> defects = createPrepareValidateAndReplace(testFolder, createPropertyResolveManager(propertiesToUseWhileResolving));

        Assert.assertEquals("We shouldnt get any defects.", 0, defects.size());

        File expectedArchive = new File(targetResourceFolder, "expectedResult/archive.jar");
        File resultArchive = new File(targetResourceFolder, "package/archive.jar");

        JarInputStream expected = new JarInputStream(new FileInputStream(expectedArchive));
        JarInputStream result = new JarInputStream(new FileInputStream(resultArchive));

        Assert.assertNotNull("SRC jar should contain the manifest as first entry", expected.getManifest());
        Assert.assertNotNull("RESULT jar should contain the manifest as first entry", result.getManifest());

        expected.close();
        result.close();

        checkIfResultIsAsExpected(testFolder);
    }

    @Test
    public void checkJarInEar() {
        String testFolder = "testArchive/correct/jarInEar";

        LinkedHashSet<Defect> defects = createPrepareValidateAndReplace(testFolder, createPropertyResolveManager(propertiesToUseWhileResolving));

        Assert.assertEquals("We shouldnt get any defects.", 0, defects.size());

        checkIfResultIsAsExpected(testFolder);
    }

    @Test
    public void checkJarInEarWithRegExp() {
        String testFolder = "testArchive/correct/jarInEarWithRegExp";

        LinkedHashSet<Defect> defects = createPrepareValidateAndReplace(testFolder, createPropertyResolveManager(propertiesToUseWhileResolving));

        Assert.assertEquals("We shouldnt get any defects.", 0, defects.size());

        checkIfResultIsAsExpected(testFolder);
    }

    @Test
    public void checkJarWhereTheSourceIsntAJarPerDefinition() throws ArchiveException, IOException {
        LoggingUtils.setLogLevel(logger, Level.ERROR);

        String testFolder = "testArchive/correct/jarWhereSourceIsntAJarPerDefinition";

        File testResourceFolder = new File("src/test/resources/", testFolder);
        File targetResourceFolder = new File("target/test-resources/", testFolder);

        LinkedHashSet<Defect> defects = createPrepareValidateAndReplace(testFolder, createPropertyResolveManager(propertiesToUseWhileResolving));

        Assert.assertEquals("We shouldnt get any defects.", 0, defects.size());

        JarInputStream in = new JarInputStream(new FileInputStream(new File(testResourceFolder, "package/archive.jar")));
        JarInputStream out = new JarInputStream(new FileInputStream(new File(targetResourceFolder, "package/archive.jar")));

        Assert.assertNull("SRC jar should be a jar which is packed via zip, so the first entry isn't the manifest.", in.getManifest());
        Assert.assertNotNull("RESULT jar should contain the manifest as first entry", out.getManifest());

        in.close();
        out.close();

        checkIfResultIsAsExpected(testFolder);
    }

    @Test
    public void checkTar() {
        String testFolder = "testArchive/correct/tar";

        LinkedHashSet<Defect> defects = createPrepareValidateAndReplace(testFolder, createPropertyResolveManager(propertiesToUseWhileResolving));

        Assert.assertEquals("We shouldnt get any defects.", 0, defects.size());

        checkIfResultIsAsExpected(testFolder);
    }

    @Test
    public void checkZip() {
        String testFolder = "testArchive/correct/zip";

        LinkedHashSet<Defect> defects = createPrepareValidateAndReplace(testFolder, createPropertyResolveManager(propertiesToUseWhileResolving));

        Assert.assertEquals("We shouldnt get any defects.", 0, defects.size());

        checkIfResultIsAsExpected(testFolder);
    }

    @Test
    public void checkBigZip() {
        String testFolder = "testArchive/correct/bigZip";

        LinkedHashSet<Defect> defects = createPrepareValidateAndReplace(testFolder, createPropertyResolveManager(propertiesToUseWhileResolving));

        Assert.assertEquals("We shouldnt get any defects.", 0, defects.size());

        checkIfResultIsAsExpected(testFolder);
    }

    @Test
    public void checkUnkownArchiveType() {
        String testFolder = "testArchive/wrong/unkownArchiveType";

        LinkedHashSet<Defect> defects = createPrepareValidateAndReplace(testFolder, createPropertyResolveManager(propertiesToUseWhileResolving));

        Assert.assertEquals("We should get a defect.", 1, defects.size());
        Assert.assertEquals("We expect ArchiveTypeNotImplementedDefect", ArchiveTypeNotImplementedDefect.class, defects.iterator().next().getClass());
    }

    @Test
    public void checkDuplicateArchiveEntry() {
        String testFolder = "testArchive/wrong/duplicateEntry";

        LinkedHashSet<Defect> defects = createPrepareAndExecuteValidator(testFolder, createPropertyResolveManager(propertiesToUseWhileResolving));

        Assert.assertEquals("We should get a defect.", 1, defects.size());
        Assert.assertEquals("We expect ArchiveTypeNotImplementedDefect", ArchiveDuplicateDefinedInPMarkerDefect.class, defects.iterator().next().getClass());
    }

    @Test
    public void checkNotReplacedProperty() {
        String testFolder = "testArchive/wrong/notReplacedProperty";

        LinkedHashSet<Defect> result = createPrepareAndReplace(testFolder, createPropertyResolveManager(propertiesToUseWhileResolving));
        List<Defect> defects = new ArrayList<Defect>(result);

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
        String testFolder = "testArchive/wrong/targetFileDoesNotExist";

        LinkedHashSet<Defect> result = createPrepareAndExecuteValidator(testFolder, createPropertyResolveManager(propertiesToUseWhileResolving));
        List<Defect> defects = new ArrayList<Defect>(result);

        Assert.assertEquals("We should get a defect.", 1, defects.size());
        Assert.assertEquals("We expect FileDoesNotExistDefect", FileDoesNotExistDefect.class, defects.get(0).getClass());
    }

    @Test
    public void checkPlaceholderDoesNotExist() {
        String testFolder = "testArchive/wrong/placeholderDoesNotExist";

        LinkedHashSet<Defect> result = createPrepareAndExecuteValidator(testFolder, createPropertyResolveManager(propertiesToUseWhileResolving));
        List<Defect> defects = new ArrayList<Defect>(result);

        Assert.assertEquals("We should get a defect.", 3, defects.size());
        Assert.assertEquals("We expect NoPlaceholderInTargetFileDefect", NoPlaceholderInTargetFileDefect.class, defects.get(0).getClass());
        Assert.assertEquals("We expect PlaceholderNotDefinedDefect.", PlaceholderNotDefinedDefect.class, defects.get(1).getClass());
        Assert.assertEquals("We expect missingProperty", PropertyNotDefinedInResolverDefect.class, defects.get(2).getClass());
    }

    @Test
    public void checkDuplicatePropertyEntry() {
        String testFolder = "testArchive/wrong/duplicatePropertyEntry";

        LinkedHashSet<Defect> result = createPrepareAndExecuteValidator(testFolder, createPropertyResolveManager(propertiesToUseWhileResolving));
        List<Defect> defects = new ArrayList<Defect>(result);

        Assert.assertEquals("We should get a defect.", 2, defects.size());
        Assert.assertEquals("We expect PropertyDuplicateDefinedInPMarkerDefect", PropertyDuplicateDefinedInPMarkerDefect.class, defects.get(0).getClass());
        Assert.assertEquals("We expect PlaceholderNotDefinedDefect.", PlaceholderNotDefinedDefect.class, defects.get(1).getClass());
        Assert.assertEquals("We expect missingProperty", "foobar2", ((PropertyDuplicateDefinedInPMarkerDefect) defects.get(0)).getPProperty().getName());
    }

    @Test
    public void checkWrongPacifyFilter() {
        String testFolder = "testArchive/wrong/wrongPacifyFilter";

        LinkedHashSet<Defect> result = createPrepareAndExecuteValidator(testFolder, createPropertyResolveManager(propertiesToUseWhileResolving));
        List<Defect> defects = new ArrayList<Defect>(result);

        Assert.assertEquals("We should get a defect.", 2, defects.size());
        Assert.assertEquals("We expect FilterNotFoundDefect", FilterNotFoundDefect.class, defects.get(0).getClass());
        Assert.assertEquals("We expect PlaceholderNotDefinedDefect.", PlaceholderNotDefinedDefect.class, defects.get(1).getClass());
        Assert.assertEquals("We expect missing.filter.class", "missing.filter.class", ((FilterNotFoundDefect) defects.get(0)).getPFile().getFilterClass());
    }

}
