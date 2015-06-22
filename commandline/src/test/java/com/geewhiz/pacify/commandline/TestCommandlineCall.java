package com.geewhiz.pacify.commandline;

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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;

import com.geewhiz.pacify.TestUtil;

public class TestCommandlineCall {

    @Test
    public void testReplace() {

        File testBasePath = new File("target/test-classes/testReplace");
        File myTestProperty = new File(testBasePath, "properties/myTest.properties");
        File myPackagePath = new File(testBasePath, "package");
        File myResultPath = new File(testBasePath, "result");

        int result = PacifyViaCommandline.mainInternal(new String[] {
                "--info",
                "replace",
                "--envName=local",
                "--resolvers=FileResolver",
                "--packagePath=" + myPackagePath.getAbsolutePath(),
                "--createCopy=false",
                "-RFileResolver.file=" + myTestProperty.getAbsolutePath()
        });

        Assert.assertEquals("Configuration returned with errors.", 0, result);

        TestUtil.checkIfResultIsAsExpected(myPackagePath, myResultPath);
    }

    @Test
    public void testValidateMarkerFiles() {
        File testBasePath = new File("target/test-classes/testValidate");

        int result = PacifyViaCommandline.mainInternal(new String[] {
                "validateMarkerFiles",
                "--packagePath=" + testBasePath
        });

        Assert.assertEquals("Validate returned with errors.", 0, result);
    }

    @Test
    public void testValidateWithProperties() {
        File testBasePath = new File("target/test-classes/testValidate");
        File myTestProperty = new File(testBasePath, "properties/myTest.properties");

        int result = PacifyViaCommandline.mainInternal(new String[] {
                "validate",
                "--resolvers=FileResolver",
                "--packagePath=" + testBasePath,
                "-RFileResolver.file=" + myTestProperty.getAbsolutePath()
        });

        Assert.assertEquals("Validate returned with errors.", 0, result);
    }

    @Test
    public void testHelp() {
        int result = PacifyViaCommandline.mainInternal(new String[] {
                "--help"
        });

        Assert.assertEquals("Call should return 0.", 0, result);
    }

    @Test
    public void testShowUsedProperties() throws Exception {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream oldStdOut = System.out;
        System.setOut(new PrintStream(outContent));

        File testBasePath = new File("target/test-classes/testShowUsedProperties");

        int result = PacifyViaCommandline.mainInternal(new String[] {
                "showUsedProperties",
                "--packagePath=" + testBasePath
        });

        Assert.assertEquals("ShowUsedProperties returned with errors.", 0, result);

        Assert.assertEquals(FileUtils.readFileToString(new File(testBasePath + "/result/result.txt")), outContent.toString().trim());

        System.setOut(oldStdOut);
    }

}
