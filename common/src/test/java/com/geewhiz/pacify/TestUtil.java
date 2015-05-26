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

import org.apache.tools.ant.util.FileUtils;
import org.junit.Assert;

import com.geewhiz.pacify.model.utils.DirFilter;

public class TestUtil {

    public static void checkIfResultIsAsExpected(File startPath) {
        File dirWithFilesWhichTheyShouldLookLike = new File(startPath.getPath() + "_ResultFiles");
        List<File> filesToCompare = getFiles(dirWithFilesWhichTheyShouldLookLike);
        for (File resultFile : filesToCompare) {
            String completeRelativePath = dirWithFilesWhichTheyShouldLookLike.getPath();
            int index = resultFile.getPath().indexOf(completeRelativePath) + completeRelativePath.length();
            String relativePath = resultFile.getPath().substring(index);

            File filteredFile = new File(startPath, relativePath);
            try {
                Assert.assertTrue(
                        "Filtered file does not have the expected result. The content of the File should look like ["
                                + resultFile.getPath() + "] but is [" + filteredFile.getPath() + "]."
                        , FileUtils.getFileUtils().contentEquals(resultFile, filteredFile));

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
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
}
