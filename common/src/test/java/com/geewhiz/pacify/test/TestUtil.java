/*-
 * ========================LICENSE_START=================================
 * com.geewhiz.pacify.common
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

package com.geewhiz.pacify.test;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.junit.Assert;
import org.junit.Ignore;

import com.geewhiz.pacify.model.utils.DirFilter;
import com.geewhiz.pacify.utils.ArchiveUtils;

@Ignore
public class TestUtil {

    public static void checkIfResultIsAsExpected(String testFolder) {
        File targetResourceFolder = new File("target/test-resources/", testFolder);

        File actual = new File(targetResourceFolder, "package");
        File expected = new File(targetResourceFolder, "expectedResult");

        checkIfResultIsAsExpected(actual, expected);

    }

    public static void checkIfResultIsAsExpected(File actual, File expected) {
        checkIfResultIsAsExpected(actual, expected, "UTF-8");
    }

    public static void checkIfResultIsAsExpected(File actual, File expected, String encoding) {
        if (!actual.isDirectory()) {
            throw new IllegalArgumentException("checkFoler [" + actual.getAbsolutePath() + "] not a folder");
        }

        if (!expected.isDirectory()) {
            throw new IllegalArgumentException("resultFolder [" + expected.getAbsolutePath() + "] not a folder");
        }

        // Look that the files exists and are like we want it
        for (File expectedFile : getFiles(expected)) {
            String completeRelativePath = expected.getPath();
            int index = expectedFile.getPath().indexOf(completeRelativePath) + completeRelativePath.length();
            String relativePath = expectedFile.getPath().substring(index);

            File filteredFile = new File(actual, relativePath);
            try {
                Assert.assertEquals("Both files exists.", expectedFile.exists(), filteredFile.exists());

                if (ArchiveUtils.isArchiveAndIsSupported(expectedFile.getName())) {
                    checkArchiveIsAsExpected(filteredFile, expectedFile);
                } else {
                    Assert.assertEquals("File [" + filteredFile.getPath() + "] doesnt look like [" + expectedFile.getPath() + "].\n",
                            FileUtils.readFileToString(expectedFile, encoding), FileUtils.readFileToString(filteredFile, encoding));
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ArchiveException e) {
                throw new RuntimeException(e);
            }
        }

        // Check if there are more files than expected
        for (File actualFile : getFiles(actual)) {
            String completeRelativePath = actual.getPath();
            int index = actualFile.getPath().indexOf(completeRelativePath) + completeRelativePath.length();
            String relativePath = actualFile.getPath().substring(index);

            File expectedFile = new File(expected, relativePath);
            Assert.assertEquals("There is a file which is not expected [" + actualFile.getPath() + "].", expectedFile.exists(), actualFile.exists());
        }

    }

    public static URL getURLForFile(File file) {
        try {
            return file.toURI().toURL();
        } catch (MalformedURLException e) {
            Assert.fail();
        }
        throw new RuntimeException("Shouldn't reach this code!");
    }

    private static List<File> getFiles(File folder) {
        List<File> files = new ArrayList<File>();

        Collections.addAll(files, folder.listFiles(new FileFilter() {
            public boolean accept(File pathName) {
                return pathName.isFile();
            }
        }));

        for (File subFolder : folder.listFiles(new DirFilter())) {
            files.addAll(getFiles(subFolder));
        }

        return files;
    }

    public static void removeOldTestResourcesAndCopyAgain(File fromFolder, File toFolder) {
        if (toFolder.exists()) {
            FileUtils.deleteQuietly(toFolder);
        }

        try {
            FileUtils.copyDirectory(fromFolder, toFolder, true);
        } catch (IOException e) {
            throw new RuntimeException("error while copy test-resources", e);
        }

    }

    public static ListAppender addListAppenderToLogger() {
        ListAppender result = new ListAppender();

        // get the root logger
        Logger logger = LogManager.getLogger("");

        LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        Configuration config = ctx.getConfiguration();
        config.addLoggerAppender((org.apache.logging.log4j.core.Logger) logger, result);

        return result;
    }

    public static void checkArchiveIsAsExpected(File replacedArchive, File expectedArchive) throws ArchiveException, IOException {
        archiveDoesNotContainAdditionEntries(replacedArchive, expectedArchive);
        archiveContainsEntries(replacedArchive, expectedArchive);
    }

    private static void archiveContainsEntries(File replacedArchive, File expectedArchive) throws ArchiveException, IOException {
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

                if (ArchiveUtils.isArchiveAndIsSupported(expectedEntry.getName())) {
                    Assert.assertTrue("we expect a archive", ArchiveUtils.isArchiveAndIsSupported(replacedEntry.getName()));

                    File replacedChildArchive = ArchiveUtils.extractFile(replacedArchive, ArchiveUtils.getArchiveType(replacedArchive),
                            replacedEntry.getName());
                    File expectedChildArchive = ArchiveUtils.extractFile(expectedArchive, ArchiveUtils.getArchiveType(expectedArchive),
                            expectedEntry.getName());

                    archiveContainsEntries(replacedChildArchive, expectedChildArchive);

                    replacedChildArchive.delete();
                    expectedChildArchive.delete();

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

    private static void archiveDoesNotContainAdditionEntries(File replacedArchive, File expectedArchive) throws ArchiveException, IOException {
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

                if (ArchiveUtils.isArchiveAndIsSupported(expectedEntry.getName())) {
                    Assert.assertTrue("we expect a archive", ArchiveUtils.isArchiveAndIsSupported(replacedEntry.getName()));

                    File replacedChildArchive = ArchiveUtils.extractFile(replacedArchive, ArchiveUtils.getArchiveType(replacedArchive),
                            replacedEntry.getName());
                    File expectedChildArchive = ArchiveUtils.extractFile(expectedArchive, ArchiveUtils.getArchiveType(expectedArchive),
                            expectedEntry.getName());

                    archiveDoesNotContainAdditionEntries(replacedChildArchive, expectedChildArchive);

                    replacedChildArchive.delete();
                    expectedChildArchive.delete();
                }

                break;
            }

            expectedIS.close();
            Assert.assertTrue("Entry [" + replacedEntry.getName() + "] is not in the expected archive. This file shouldn't exist.", entryFound);
        }

        replacedIS.close();

    }

    private static ByteArrayOutputStream readContent(ArchiveInputStream ais) throws IOException {
        byte[] content = new byte[2048];
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        BufferedOutputStream bos = new BufferedOutputStream(result);

        int len;
        while ((len = ais.read(content)) != -1) {
            bos.write(content, 0, len);
        }
        bos.close();
        content = null;

        return result;
    }

}
