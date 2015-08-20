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

public class TestMultipleResolvers {

    @Test
    public void testAll() {
        File testResourceFolder = new File("src/test/resources/TestMultipleResolver");
        File targetResourceFolder = new File("target/test-resources/TestMultipleResolver");

        TestUtil.removeOldTestResourcesAndCopyAgain(testResourceFolder, targetResourceFolder);

        File myTestProperty = new File(targetResourceFolder, "properties/myProperties.properties");
        File myPackagePath = new File(targetResourceFolder, "package");
        File myExpectedResultPath = new File(targetResourceFolder, "expectedResult");

        PacifyViaCommandline pacifyViaCommandline = new PacifyViaCommandline();

        int result = pacifyViaCommandline.mainInternal(new String[] {
                "--debug",
                "replace",
                "--packagePath=" + myPackagePath,
                "--resolvers=CmdResolver,FileResolver",
                "-RFileResolver.file=" + myTestProperty.getAbsolutePath(),
                "-RCmdResolver.foobar7=anotherValue"
        });

        Assert.assertEquals("Configuration returned with errors.", 0, result);

        TestUtil.checkIfResultIsAsExpected(myPackagePath, myExpectedResultPath);
    }

    @Test
    public void testAllWithCustomTokens() {
        File testResourceFolder = new File("src/test/resources/TestMultipleResolverWithCustomTokens");
        File targetResourceFolder = new File("target/test-resources/TestMultipleResolver");

        TestUtil.removeOldTestResourcesAndCopyAgain(testResourceFolder, targetResourceFolder);

        File myTestProperty = new File(targetResourceFolder, "properties/myProperties.properties");
        File myPackagePath = new File(targetResourceFolder, "package");
        File myExpectedResultPath = new File(targetResourceFolder, "expectedResult");

        PacifyViaCommandline pacifyViaCommandline = new PacifyViaCommandline();

        int result = pacifyViaCommandline.mainInternal(new String[] {
                "--debug",
                "replace",
                "--packagePath=" + myPackagePath,
                "--resolvers=CmdResolver,FileResolver",
                "-RFileResolver.file=" + myTestProperty.getAbsolutePath(),
                "-RCmdResolver.foobar7=anotherValue"
        });

        Assert.assertEquals("Configuration returned with errors.", 0, result);

        TestUtil.checkIfResultIsAsExpected(myPackagePath, myExpectedResultPath);
    }

    @Test
    public void testAllOnCopy() {
        File testResourceFolder = new File("src/test/resources/TestMultipleResolverWithCustomTokens");
        File targetResourceFolder = new File("target/test-resources/TestMultipleResolverWithCustomTokens");

        TestUtil.removeOldTestResourcesAndCopyAgain(testResourceFolder, targetResourceFolder);

        File myTestProperty = new File(targetResourceFolder, "properties/myProperties.properties");
        File myPackagePath = new File(targetResourceFolder, "package");
        File myExpectedResultPath = new File(targetResourceFolder, "expectedResult");
        File destinationPath = new File(targetResourceFolder, "copyOfOriginal");

        PacifyViaCommandline pacifyViaCommandline = new PacifyViaCommandline();

        int result = pacifyViaCommandline.mainInternal(new String[] {
                "replace",
                "--packagePath=" + myPackagePath.getAbsolutePath(),
                "--copyTo=" + destinationPath.getAbsolutePath(),
                "--resolvers=CmdResolver,FileResolver",
                "-RFileResolver.file=" + myTestProperty.getAbsolutePath(),
                "-RCmdResolver.foobar7=anotherValue"
        });

        Assert.assertEquals("Configuration returned with errors.", 0, result);

        // the original package should not be touched
        TestUtil.checkIfResultIsAsExpected(myPackagePath, new File(testResourceFolder, "package"));
        TestUtil.checkIfResultIsAsExpected(destinationPath, myExpectedResultPath);
    }

    @Test
    public void testDifferentEncodings() {
        File testResourceFolder = new File("src/test/resources/TestDifferentEncodings");
        File targetResourceFolder = new File("target/test-resources/TestDifferentEncodings");

        TestUtil.removeOldTestResourcesAndCopyAgain(testResourceFolder, targetResourceFolder);

        File myTestProperty = new File(targetResourceFolder, "properties/utf16.properties");
        File myPackagePath = new File(targetResourceFolder, "package");
        File myExpectedResultPath = new File(targetResourceFolder, "expectedResult");

        PacifyViaCommandline pacifyViaCommandline = new PacifyViaCommandline();

        int result = pacifyViaCommandline.mainInternal(new String[] {
                "--debug",
                "replace",
                "--packagePath=" + myPackagePath,
                "--resolvers=CmdResolver,FileResolver",
                "-RFileResolver.file=" + myTestProperty.getAbsolutePath(),
                "-RFileResolver.encoding=UTF-16",
                "-RCmdResolver.foobar7=anotherValue"
        });

        Assert.assertEquals("Configuration returned with errors.", 0, result);

        TestUtil.checkIfResultIsAsExpected(myPackagePath, myExpectedResultPath, "ASCII");
    }
}
