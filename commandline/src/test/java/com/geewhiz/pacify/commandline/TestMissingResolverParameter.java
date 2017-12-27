/*-
 * ========================LICENSE_START=================================
 * com.geewhiz.pacify.commandline
 * %%
 * Copyright (C) 2011 - 2017 Sven Oppermann
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */

package com.geewhiz.pacify.commandline;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import com.geewhiz.pacify.test.ListAppender;
import com.geewhiz.pacify.test.TestUtil;



public class TestMissingResolverParameter {

    @Test
    public void testMissingPropertyParameter() throws IOException {
        File testBasePath = new File("target/test-classes/MissingPackage");
        File myTestProperty = new File(testBasePath, "properties/MissingProperty.properties");
        File myPackagePath = new File(testBasePath, "package");

        ListAppender listAppender = TestUtil.addListAppenderToLogger();

        PacifyViaCommandline pacifyViaCommandline = new PacifyViaCommandline();
        int result = pacifyViaCommandline.mainInternal(new String[] {
                "replace",
                "--packagePath=" + myPackagePath,
                "--resolvers=FileResolver",
                "-RFileResolver.file=" + myTestProperty.getAbsolutePath()
        });

        Assert.assertEquals("We expect an error.", 1, result);
        Assert.assertEquals("We expect two lines in the output.", 2, listAppender.getLogMessages().size());
        Assert.assertEquals("We expect the defect PropertyFileNotFound.", "==== !!!!!! We got Errors !!!!! ...", listAppender.getLogMessages().get(0));
        Assert.assertEquals("We expect the defect PropertyFileNotFound.", "PropertyFileNotFound:", listAppender.getLogMessages().get(1).split("\\n")[0]);
    }
}
