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

import com.geewhiz.pacify.TestUtil;

public class TestMultipleResolvers {

    @Test
    public void testAll() {
        File testBasePath = new File("target/test-classes/TestMultipleResolver");
        File myTestProperty = new File(testBasePath, "properties/myProperties.properties");
        File myPackagePath = new File(testBasePath, "package");
        File myResultPath = new File(testBasePath, "result");

        int result = PacifyViaCommandline.mainInternal(new String[] {
                "--debug",
                "replace",
                "--envName=local",
                "--resolvers=CmdResolver,FileResolver",
                "--packagePath=" + myPackagePath,
                "--createCopy=false",
                "-DFileResolver.file=" + myTestProperty.getAbsolutePath(),
                "-DCmdResolver.foobar7=anotherValue"
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
                "--envName=local",
                "--resolvers=CmdResolver,FileResolver",
                "--packagePath=" + myPackagePath,
                "--createCopy=false",
                "-DFileResolver.file=" + myTestProperty.getAbsolutePath(),
                "-DCmdResolver.foobar7=anotherValue"
        });

        Assert.assertEquals("Configuration returned with errors.", 0, result);

        TestUtil.checkIfResultIsAsExpected(myPackagePath, myResultPath);
    }

    @Test
    public void testAllOnCopy() {
        String envName = "test";

        File testBasePath = new File("target/test-classes/TestMultipleResolverOnCopy");
        File myTestProperty = new File(testBasePath, "properties/myProperties.properties");
        File myPackagePath = new File(testBasePath, "package");
        File myResultPath = new File(testBasePath, "result");

        File destinationPath = new File(myPackagePath.getAbsolutePath() + "_" + envName);

        int result = PacifyViaCommandline.mainInternal(new String[] {
                "replace",
                "--envName=" + envName,
                "--resolvers=CmdResolver,FileResolver",
                "--packagePath=" + myPackagePath.getAbsolutePath(),
                "-DFileResolver.file=" + myTestProperty.getAbsolutePath(),
                "-DCmdResolver.foobar7=anotherValue"
        });

        Assert.assertEquals("Configuration returned with errors.", 0, result);

        TestUtil.checkIfResultIsAsExpected(destinationPath, myResultPath);
    }
}
