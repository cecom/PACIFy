package com.geewhiz.pacify.commandline;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.geewhiz.pacify.TestUtil;

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

public class TestShowUsedProperties {

    @BeforeClass
    public static void removeOldData() {
        TestUtil.removeOldTestResourcesAndCopyAgain();
    }

    @Test
    public void writeToStdout() throws Exception {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream oldStdOut = System.out;
        System.setOut(new PrintStream(outContent));

        File testBasePath = new File("target/test-classes/testShowUsedProperties");
        File packagePath = new File(testBasePath, "package");

        int result = PacifyViaCommandline.mainInternal(new String[] {
                "showUsedProperties",
                "--packagePath=" + packagePath
        });

        Assert.assertEquals("ShowUsedProperties returned with errors.", 0, result);

        Assert.assertEquals(FileUtils.readFileToString(new File(testBasePath + "/result/result.txt")), outContent.toString());

        System.setOut(oldStdOut);
    }

    @Test
    public void writeToStdoutWithPrefix() throws Exception {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream oldStdOut = System.out;
        System.setOut(new PrintStream(outContent));

        File testBasePath = new File("target/test-classes/testShowUsedProperties");
        File packagePath = new File(testBasePath, "package");

        int result = PacifyViaCommandline.mainInternal(new String[] {
                "showUsedProperties",
                "--packagePath=" + packagePath,
                "--outputPrefix=###"
        });

        Assert.assertEquals("ShowUsedProperties returned with errors.", 0, result);

        Assert.assertEquals(FileUtils.readFileToString(new File(testBasePath + "/result/resultWithPrefix.txt")), outContent.toString());

        System.setOut(oldStdOut);
    }

    @Test
    public void writeToFile() throws Exception {
        File testBasePath = new File("target/test-classes/testShowUsedProperties");
        File packagePath = new File(testBasePath, "package");
        File resultFile = new File(testBasePath, "result/result.txt");
        File destinationFile = new File(testBasePath, "output/output.txt");

        destinationFile.delete();

        int result = PacifyViaCommandline.mainInternal(new String[] {
                "showUsedProperties",
                "--packagePath=" + packagePath,
                "--destinationFile=" + destinationFile
        });

        Assert.assertEquals("ShowUsedProperties returned with errors.", 0, result);
        Assert.assertTrue("Content is same", FileUtils.contentEquals(resultFile, destinationFile));
    }

    @Test
    public void writeToFileWithPrefix() throws Exception {
        File testBasePath = new File("target/test-classes/testShowUsedProperties");
        File packagePath = new File(testBasePath, "package");
        File resultFile = new File(testBasePath, "result/resultWithPrefix.txt");
        File destinationFile = new File(testBasePath, "output/outputWithPrefix.txt");

        destinationFile.delete();

        int result = PacifyViaCommandline.mainInternal(new String[] {
                "showUsedProperties",
                "--packagePath=" + packagePath,
                "--outputPrefix=###",
                "--destinationFile=" + destinationFile
        });

        Assert.assertEquals("ShowUsedProperties returned with errors.", 0, result);
        Assert.assertTrue("Content is same", FileUtils.contentEquals(resultFile, destinationFile));
    }
}
