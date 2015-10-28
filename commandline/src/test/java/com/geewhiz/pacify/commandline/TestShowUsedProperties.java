package com.geewhiz.pacify.commandline;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;

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

    @Test
    public void writeToStdout() throws Exception {

        PrintStream oldStdOut = System.out;

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        File testBasePath = new File("target/test-classes/testShowUsedProperties");
        File packagePath = new File(testBasePath, "package");

        int result = 0;
        try {
            PacifyViaCommandline pacifyViaCommandline = new PacifyViaCommandline();
            result = pacifyViaCommandline.mainInternal(new String[] {
                    "showUsedProperties",
                    "--packagePath=" + packagePath
            });
        }
        finally {
            System.setOut(oldStdOut);
        }

        outContent.close();

        Assert.assertEquals("ShowUsedProperties should not return an error.", 0, result);
        Assert.assertEquals(FileUtils.readFileToString(new File(testBasePath + "/expectedResult/result.txt")),
                outContent.toString());
    }

    @Test
    public void writeToStdoutWithPrefix() throws Exception {
        PrintStream oldStdOut = System.out;

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        File testBasePath = new File("target/test-classes/testShowUsedProperties");
        File packagePath = new File(testBasePath, "package");

        int result = 0;
        try {
            PacifyViaCommandline pacifyViaCommandline = new PacifyViaCommandline();

            result = pacifyViaCommandline.mainInternal(new String[] {
                    "showUsedProperties",
                    "--packagePath=" + packagePath,
                    "--outputPrefix=###"
            });
        }
        finally {
            System.setOut(oldStdOut);
        }

        outContent.close();

        Assert.assertEquals("ShowUsedProperties should not return an error.", 0, result);
        Assert.assertEquals(FileUtils.readFileToString(new File(testBasePath + "/expectedResult/resultWithPrefix.txt")),
                outContent.toString());
    }

    @Test
    public void writeToFile() throws Exception {
        File targetResourceFolder = new File("target/test-classes/testShowUsedProperties");

        File packagePath = new File(targetResourceFolder, "package");
        File expectedResult = new File(targetResourceFolder, "expectedResult/result.txt");
        File destinationFile = new File(targetResourceFolder, "result/result.txt");

        destinationFile.delete();

        PacifyViaCommandline pacifyViaCommandline = new PacifyViaCommandline();

        int result = pacifyViaCommandline.mainInternal(new String[] {
                "showUsedProperties",
                "--packagePath=" + packagePath,
                "--destinationFile=" + destinationFile
        });

        Assert.assertEquals("ShowUsedProperties should not return an error.", 0, result);
        Assert.assertTrue("Content is same", FileUtils.contentEquals(expectedResult, destinationFile));
    }

    @Test
    public void writeToFileWithPrefix() throws Exception {
        File targetResourceFolder = new File("target/test-classes/testShowUsedProperties");

        File packagePath = new File(targetResourceFolder, "package");
        File resultFile = new File(targetResourceFolder, "expectedResult/resultWithPrefix.txt");
        File destinationFile = new File(targetResourceFolder, "result/resultWithPrefix.txt");

        destinationFile.delete();

        PacifyViaCommandline pacifyViaCommandline = new PacifyViaCommandline();

        int result = pacifyViaCommandline.mainInternal(new String[] {
                "showUsedProperties",
                "--packagePath=" + packagePath,
                "--outputPrefix=###",
                "--destinationFile=" + destinationFile
        });

        Assert.assertEquals("ShowUsedProperties returned with errors.", 0, result);
        Assert.assertTrue("Content is same", FileUtils.contentEquals(resultFile, destinationFile));
    }
}
