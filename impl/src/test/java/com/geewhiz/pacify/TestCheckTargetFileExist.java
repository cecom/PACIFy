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

import static org.hamcrest.CoreMatchers.startsWith;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import com.geewhiz.pacify.checks.impl.CheckTargetFileExist;
import com.geewhiz.pacify.defect.Defect;
import com.geewhiz.pacify.defect.FileDoesNotExistDefect;
import com.geewhiz.pacify.managers.EntityManager;
import com.geewhiz.pacify.model.PFile;
import com.geewhiz.pacify.model.PMarker;
import com.geewhiz.pacify.utils.LoggingUtils;

public class TestCheckTargetFileExist extends TestBase {

    @Test
    public void checkPFileForCorrect() {
        String testFolder = "checkTargetFileExistTest/correct/file";

        LinkedHashSet<Defect> defects = createPrepareAndExecuteValidator(testFolder, createPropertyResolveManager(Collections.<String, String> emptyMap()),
                new CheckTargetFileExist());

        Assert.assertEquals(0, defects.size());
    }

    @Test
    public void checkPFileForNotCorrect() {
        String testFolder = "checkTargetFileExistTest/wrong/file";

        LinkedHashSet<Defect> defects = createPrepareAndExecuteValidator(testFolder, createPropertyResolveManager(Collections.<String, String> emptyMap()),
                new CheckTargetFileExist());

        Assert.assertEquals(1, defects.size());
    }

    @Test
    public void checkArchiveCorrect() {
        String testFolder = "checkTargetFileExistTest/correct/archive";

        LinkedHashSet<Defect> defects = createPrepareAndExecuteValidator(testFolder, createPropertyResolveManager(Collections.<String, String> emptyMap()),
                new CheckTargetFileExist());

        Assert.assertEquals(0, defects.size());
    }

    @Test
    public void checkArchiveForNotCorrect() {
        String testFolder = "checkTargetFileExistTest/wrong/archive";

        LinkedHashSet<Defect> defects = createPrepareAndExecuteValidator(testFolder, createPropertyResolveManager(Collections.<String, String> emptyMap()),
                new CheckTargetFileExist());

        Assert.assertEquals(1, defects.size());
    }

    @Test
    public void checkRegExPFile() {
        File source = new File("target/test-classes/checkTargetFileExistTest/correct/regExFile/package");

        EntityManager entityManager = new EntityManager(source);
        entityManager.initialize();

        PMarker pMarker = entityManager.getPMarkers().get(0);

        List<PFile> pFiles = entityManager.getPFilesFrom(pMarker);

        Assert.assertEquals(3, pFiles.size());

        Assert.assertEquals("file1.conf", pFiles.get(0).getRelativePath());
        Assert.assertEquals("subfolder/file2.conf", pFiles.get(1).getRelativePath());
        Assert.assertEquals("subfolder/subfolder2/file3.conf", pFiles.get(2).getRelativePath());

        Assert.assertEquals(1, pFiles.get(0).getPProperties().size());
        Assert.assertEquals(1, pFiles.get(1).getPProperties().size());
        Assert.assertEquals(1, pFiles.get(2).getPProperties().size());

        Assert.assertEquals("foobar1", pFiles.get(0).getPProperties().get(0).getName());
        Assert.assertEquals("foobar1", pFiles.get(1).getPProperties().get(0).getName());
        Assert.assertEquals("foobar1", pFiles.get(2).getPProperties().get(0).getName());
    }

    @Test
    public void checkRegExPArchive() {
        File source = new File("target/test-classes/checkTargetFileExistTest/correct/regExArchive/package");

        EntityManager entityManager = new EntityManager(source);
        entityManager.initialize();

        PMarker pMarker = entityManager.getPMarkers().get(0);

        List<PFile> pFiles = entityManager.getPFilesFrom(pMarker);

        Assert.assertEquals(3, pFiles.size());

        Assert.assertThat(pFiles.get(0).getRelativePath(), startsWith("file1.conf"));
        Assert.assertThat(pFiles.get(1).getRelativePath(), startsWith("subfolder/file2.conf"));
        Assert.assertThat(pFiles.get(2).getRelativePath(), startsWith("subfolder/subfolder2/file3.conf"));

        Assert.assertEquals(1, pFiles.get(0).getPProperties().size());
        Assert.assertEquals(1, pFiles.get(1).getPProperties().size());
        Assert.assertEquals(1, pFiles.get(2).getPProperties().size());

        Assert.assertEquals("foobar1", pFiles.get(0).getPProperties().get(0).getName());
        Assert.assertEquals("foobar1", pFiles.get(1).getPProperties().get(0).getName());
        Assert.assertEquals("foobar1", pFiles.get(2).getPProperties().get(0).getName());

    }

    @Test
    public void checkRegExForNotCorrectPFile() {
        String testFolder = "checkTargetFileExistTest/wrong/regExFile";

        LinkedHashSet<Defect> defects = createPrepareAndExecuteValidator(testFolder, createPropertyResolveManager(Collections.<String, String> emptyMap()),
                new CheckTargetFileExist());

        Assert.assertEquals(1, defects.size());
    }

    @Test
    public void checkRegExDoesNotMatchInArchive() throws ArchiveException, IOException {
        Logger logger = LogManager.getLogger(TestArchive.class.getName());
        LoggingUtils.setLogLevel(logger, Level.INFO);

        String testFolder = "checkTargetFileExistTest/wrong/regExArchive";

        LinkedHashSet<Defect> defects = createPrepareAndExecuteValidator(testFolder, createPropertyResolveManager(Collections.<String, String> emptyMap()),
                new CheckTargetFileExist());

        Assert.assertEquals("We should get a defect.", 1, defects.size());
        Assert.assertEquals("We expect FileDoesNotExistDefect", FileDoesNotExistDefect.class, defects.iterator().next().getClass());
    }
}
