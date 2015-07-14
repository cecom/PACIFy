package com.geewhiz.pacify;

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

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Ignore;

import com.geewhiz.pacify.model.utils.DirFilter;

@Ignore
public class TestUtil {

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
        for (File resultFile : getFiles(expected)) {
            String completeRelativePath = expected.getPath();
            int index = resultFile.getPath().indexOf(completeRelativePath) + completeRelativePath.length();
            String relativePath = resultFile.getPath().substring(index);

            File filteredFile = new File(actual, relativePath);
            try {

                Assert.assertEquals("Both files exists.", resultFile.exists(), filteredFile.exists());
                Assert.assertEquals("File [" + filteredFile.getPath() + "] doesnt look like [" + resultFile.getPath() + "].\n",
                        FileUtils.readFileToString(filteredFile, encoding), FileUtils.readFileToString(resultFile, encoding));

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        // Check if there are more files than expected
        for (File actualFile : getFiles(actual)) {
            String completeRelativePath = actual.getPath();
            int index = actualFile.getPath().indexOf(completeRelativePath) + completeRelativePath.length();
            String relativePath = actualFile.getPath().substring(index);

            File expectedFile = new File(expected, relativePath);
            Assert.assertEquals("Both files exists.", expectedFile.exists(), actualFile.exists());
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

    public static void removeOldTestResourcesAndCopyAgain() {
        File testClassesFolder = new File("target/test-classes");
        if (testClassesFolder.exists()) {
            for (File file : testClassesFolder.listFiles()) {
                if ("com".equals(file.getName())) {
                    continue;
                }
                FileUtils.deleteQuietly(file);
            }
        }

        try {
            FileUtils.copyDirectory(new File("src/test/resources"), testClassesFolder);
        } catch (IOException e) {
            throw new RuntimeException("error while copy test-resources", e);
        }

    }
}
