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

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

import com.geewhiz.pacify.test.TestUtil;

public class TestCommandlineCall {

    @Test
    public void testReplace() {
        File testResourceFolder = new File("src/test/resources/testReplace");
        File targetResourceFolder = new File("target/test-resources/testReplace");

        TestUtil.removeOldTestResourcesAndCopyAgain(testResourceFolder, targetResourceFolder);

        File myTestProperty = new File(targetResourceFolder, "properties/myTest.properties");
        File myPackagePath = new File(targetResourceFolder, "package");
        File myExpectedResultPath = new File(targetResourceFolder, "expectedResult");

        PacifyViaCommandline pacifyViaCommandline = new PacifyViaCommandline();

        int result = pacifyViaCommandline.mainInternal(new String[] {
                "--info",
                "replace",
                "--resolvers=FileResolver",
                "--packagePath=" + myPackagePath.getAbsolutePath(),
                "-RFileResolver.file=" + myTestProperty.getAbsolutePath()
        });

        Assert.assertEquals("Configuration returned with errors.", 0, result);

        TestUtil.checkIfResultIsAsExpected(myPackagePath, myExpectedResultPath);
    }

    @Test
    public void testValidateMarkerFiles() {
        File testBasePath = new File("target/test-classes/testValidate");

        PacifyViaCommandline pacifyViaCommandline = new PacifyViaCommandline();

        int result = pacifyViaCommandline.mainInternal(new String[] {
                "validateMarkerFiles",
                "--packagePath=" + testBasePath
        });

        Assert.assertEquals("Validate returned with errors.", 0, result);
    }

    @Test
    public void testValidateWithProperties() {
        File testBasePath = new File("target/test-classes/testValidate");
        File myTestProperty = new File(testBasePath, "properties/myTest.properties");

        PacifyViaCommandline pacifyViaCommandline = new PacifyViaCommandline();

        int result = pacifyViaCommandline.mainInternal(new String[] {
                "validate",
                "--resolvers=FileResolver",
                "--packagePath=" + testBasePath,
                "-RFileResolver.file=" + myTestProperty.getAbsolutePath()
        });

        Assert.assertEquals("Validate returned with errors.", 0, result);
    }

    @Test
    public void testHelp() {
        PacifyViaCommandline pacifyViaCommandline = new PacifyViaCommandline();

        int result = pacifyViaCommandline.mainInternal(new String[] {
                "--help"
        });

        Assert.assertEquals("Call should return 0.", 0, result);
    }

}
