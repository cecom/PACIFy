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

public class TestCommandlineCall {

    @Test
    public void testReplace() {

        String startPath = "target/test-classes/testReplace";

        int result = PacifyViaCommandline.mainInternal(new String[] {
                "--info",
                "replace",
                "--envName=local",
                "--resolvers=FileResolver",
                "--package=" + startPath,
                "--createCopy=false",
                "-DFileResolver.file=" + startPath + "/myTest.properties"
        });

        Assert.assertEquals("Configuration returned with errors.", 0, result);

        TestUtil.checkIfResultIsAsExpected(new File(startPath));
    }

    @Test
    public void testValidateMarkerFiles() {

        String startPath = "target/test-classes/testValidate";

        int result = PacifyViaCommandline.mainInternal(new String[] {
                "validateMarkerFiles",
                "--package=" + startPath
        });

        Assert.assertEquals("Validate returned with errors.", 0, result);
    }

    @Test
    public void testValidateWithProperties() {

        String startPath = "target/test-classes/testValidate";

        int result = PacifyViaCommandline.mainInternal(new String[] {
                "validate",
                "--resolvers=FileResolver",
                "--package=" + startPath,
                "-DFileResolver.file=" + startPath + "/myTest.properties"
        });

        Assert.assertEquals("Validate returned with errors.", 0, result);
    }
}
