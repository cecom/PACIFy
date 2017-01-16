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

import org.junit.Assert;
import org.junit.Test;

import com.geewhiz.pacify.test.TestUtil;

public class TestPreConfigure {

    @Test
    public void testPreConfigure() {
        File testResourceFolder = new File("src/test/resources/testPreConfigure");
        File targetResourceFolder = new File("target/test-resources/testPreConfigure");

        TestUtil.removeOldTestResourcesAndCopyAgain(testResourceFolder, targetResourceFolder);

        File myTestProperty = new File(targetResourceFolder, "properties/myTest.properties");
        File myPackagePath = new File(targetResourceFolder, "package");
        File myExpectedResultPath = new File(targetResourceFolder, "expectedResult");

        PacifyViaCommandline pacifyViaCommandline = new PacifyViaCommandline();

        int result = pacifyViaCommandline.mainInternal(new String[] { "--info", "preConfigure", "--resolvers=FileResolver",
                "--packagePath=" + myPackagePath.getAbsolutePath(), "-RFileResolver.file=" + myTestProperty.getAbsolutePath() });

        Assert.assertEquals("Configuration returned with errors.", 0, result);

        TestUtil.checkIfResultIsAsExpected(myPackagePath, myExpectedResultPath);
    }

}
