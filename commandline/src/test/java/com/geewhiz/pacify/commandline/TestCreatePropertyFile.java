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

public class TestCreatePropertyFile {

    @Test
    public void TestAll() {
        File testResourceFolder = new File("src/test/resources/TestCreatePropertyFile");
        File targetResourceFolder = new File("target/test-resources/TestCreatePropertyFile");

        TestUtil.removeOldTestResourcesAndCopyAgain(testResourceFolder, targetResourceFolder);

        File myTestProperty = new File(targetResourceFolder, "properties/subfolder/ChildOfChilds.properties");
        File myExpectedResultPath = new File(targetResourceFolder, "expectedResult");

        File destinationFile = new File(targetResourceFolder, "result/result.properties");

        destinationFile.delete();
        destinationFile.getParentFile().mkdirs();

        PacifyViaCommandline pacifyViaCommandline = new PacifyViaCommandline();

        int result = pacifyViaCommandline.mainInternal(new String[] {
                "createPropertyFile",
                "--resolvers=FileResolver",
                "--destinationFile=" + destinationFile.getAbsolutePath(),
                "-RFileResolver.file=" + myTestProperty.getAbsolutePath() });

        Assert.assertEquals("Resolver returned with errors.", 0, result);

        TestUtil.checkIfResultIsAsExpected(destinationFile.getParentFile(), myExpectedResultPath);
    }

    @Test
    public void TestWithOutputPrefix() {
        File testResourceFolder = new File("src/test/resources/TestCreatePropertyFileWithPrefix");
        File targetResourceFolder = new File("target/test-resources/TestCreatePropertyFileWithPrefix");

        TestUtil.removeOldTestResourcesAndCopyAgain(testResourceFolder, targetResourceFolder);

        File myTestProperty = new File(targetResourceFolder, "properties/subfolder/ChildOfChilds.properties");
        File myExpectedResultPath = new File(targetResourceFolder, "expectedResult");

        File destinationFile = new File(targetResourceFolder, "result/result.properties");

        destinationFile.delete();
        destinationFile.getParentFile().mkdirs();

        PacifyViaCommandline pacifyViaCommandline = new PacifyViaCommandline();

        int result = pacifyViaCommandline.mainInternal(new String[] {
                "createPropertyFile",
                "--outputPrefix=###",
                "--resolvers=FileResolver",
                "--destinationFile=" + destinationFile.getAbsolutePath(),
                "-RFileResolver.file=" + myTestProperty.getAbsolutePath() });

        Assert.assertEquals("Resolver returned with errors.", 0, result);

        TestUtil.checkIfResultIsAsExpected(destinationFile.getParentFile(), myExpectedResultPath);
    }
}
