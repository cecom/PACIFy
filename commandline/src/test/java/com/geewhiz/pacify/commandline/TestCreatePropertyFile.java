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

public class TestCreatePropertyFile {

    @Test
    public void TestAll() {
        File startPath = new File("target/test-classes/TestCreatePropertyFile");

        int result = PacifyViaCommandline.mainInternal(new String[] {
                "createPropertyFile",
                "--resolvers=FileResolver",
                "--destinationFile=" + startPath + "/result.properties",
                "-DFileResolver.file=" + startPath + "/subfolder/ChildOfChilds.properties" });

        Assert.assertEquals("Resolver returned with errors.", 0, result);

        TestUtil.checkIfResultIsAsExpected(startPath);
    }
}