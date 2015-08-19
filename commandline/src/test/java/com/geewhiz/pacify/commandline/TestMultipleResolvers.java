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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.geewhiz.pacify.TestUtil;

public class TestMultipleResolvers {

    @BeforeClass
    public static void removeOldData() {
        TestUtil.removeOldTestResourcesAndCopyAgain();
    }

    @Test
    public void testAll() {
        File testBasePath = new File("target/test-classes/TestMultipleResolver");
        File myTestProperty = new File(testBasePath, "properties/myProperties.properties");
        File myPackagePath = new File(testBasePath, "package");
        File myResultPath = new File(testBasePath, "result");

        int result = PacifyViaCommandline.mainInternal(new String[] {
                "--debug",
                "replace",
                "--packagePath=" + myPackagePath,
                "--resolvers=CmdResolver,FileResolver",
                "-RFileResolver.file=" + myTestProperty.getAbsolutePath(),
                "-RCmdResolver.foobar7=anotherValue"
        });

        Assert.assertEquals("Configuration returned with errors.", 0, result);

        TestUtil.checkIfResultIsAsExpected(myPackagePath, myResultPath);
    }

    @Test
    public void testAllWithCustomTokens() {
        File testBasePath = new File("target/test-classes/TestMultipleResolverWithCustomTokens");
        File myTestProperty = new File(testBasePath, "properties/myProperties.properties");
        File myPackagePath = new File(testBasePath, "package");
        File myResultPath = new File(testBasePath, "result");

        int result = PacifyViaCommandline.mainInternal(new String[] {
                "--debug",
                "replace",
                "--packagePath=" + myPackagePath,
                "--resolvers=CmdResolver,FileResolver",
                "-RFileResolver.file=" + myTestProperty.getAbsolutePath(),
                "-RCmdResolver.foobar7=anotherValue"
        });

        Assert.assertEquals("Configuration returned with errors.", 0, result);

        TestUtil.checkIfResultIsAsExpected(myPackagePath, myResultPath);
    }

    @Test
    public void testAllOnCopy() {
        File testBasePath = new File("target/test-classes/TestMultipleResolverOnCopy");
        File myTestProperty = new File(testBasePath, "properties/myProperties.properties");
        File myPackagePath = new File(testBasePath, "package");
        File myResultPath = new File(testBasePath, "result");
        File destinationPath = new File(testBasePath, "copyOfOriginal");

        int result = PacifyViaCommandline.mainInternal(new String[] {
                "replace",
                "--packagePath=" + myPackagePath.getAbsolutePath(),
                "--copyTo=" + destinationPath.getAbsolutePath(),
                "--resolvers=CmdResolver,FileResolver",
                "-RFileResolver.file=" + myTestProperty.getAbsolutePath(),
                "-RCmdResolver.foobar7=anotherValue"
        });

        Assert.assertEquals("Configuration returned with errors.", 0, result);

        TestUtil.checkIfResultIsAsExpected(destinationPath, myResultPath);
    }

    @Test
    public void testDifferentEncodings() {
        File testBasePath = new File("target/test-classes/TestDifferentEncodings");
        File myTestProperty = new File(testBasePath, "properties/utf16.properties");
        File myPackagePath = new File(testBasePath, "package");
        File myResultPath = new File(testBasePath, "result");

        int result = PacifyViaCommandline.mainInternal(new String[] {
                "--debug",
                "replace",
                "--packagePath=" + myPackagePath,
                "--resolvers=CmdResolver,FileResolver",
                "-RFileResolver.file=" + myTestProperty.getAbsolutePath(),
                "-RFileResolver.encoding=UTF-16",
                "-RCmdResolver.foobar7=anotherValue"
        });

        Assert.assertEquals("Configuration returned with errors.", 0, result);

        TestUtil.checkIfResultIsAsExpected(myPackagePath, myResultPath, "ASCII");
    }

    @Test
    public void testMissingPropertyParameter() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream oldStdOut = System.out;
        System.setOut(new PrintStream(outContent));

        File testBasePath = new File("target/test-classes/MissingPackage");
        File myTestProperty = new File(testBasePath, "properties/MissingProperty.properties");
        File myPackagePath = new File(testBasePath, "package");

        int result = 0;

        result = PacifyViaCommandline.mainInternal(new String[] {
                "replace",
                "--packagePath=" + myPackagePath,
                "--resolvers=FileResolver",
                "-RFileResolver.file=" + myTestProperty.getAbsolutePath()
        });

        Assert.assertEquals("We expect an error.", 1, result);

        Pattern p = Pattern.compile("ERROR (.*):");
        Matcher m = p.matcher(outContent.toString());

        Assert.assertTrue("We expect a defect", m.find());
        Assert.assertEquals("We expect the defect PropertyFileNotFound.", "PropertyFileNotFound", m.group(1));

        Assert.assertFalse("There should be no other defect.", m.find());
        System.setOut(oldStdOut);
    }
}
