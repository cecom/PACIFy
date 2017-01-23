/*-
 * ========================LICENSE_START=================================
 * com.geewhiz.pacify.impl
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

package com.geewhiz.pacify;

import java.io.File;

import org.junit.Test;

import com.geewhiz.pacify.ShowUsedProperties.OutputType;
import com.geewhiz.pacify.test.TestUtil;

public class TestShowUsedProperties {

    @Test
    public void testFileAndArchive() {
        File testResourceFolder = new File("target/test-classes/testShowUsedProperties/correct");
        File targetResourceFolder = new File("target/test-resources/testShowUsedProperties/correct");

        TestUtil.removeOldTestResourcesAndCopyAgain(testResourceFolder, targetResourceFolder);

        File packagePath = new File(testResourceFolder, "package");
        File resultFile = new File(targetResourceFolder, "result/output.txt");
        File expectedResultPath = new File(targetResourceFolder, "expectedResult");

        ShowUsedProperties showUsedProperties = new ShowUsedProperties();
        showUsedProperties.setPackagePath(packagePath);
        showUsedProperties.setOutputType(OutputType.File);
        showUsedProperties.setTargetFile(resultFile);
        showUsedProperties.setOutputEncoding("UTF-8");
        showUsedProperties.setOutputPrefix("");

        showUsedProperties.execute();

        TestUtil.checkIfResultIsAsExpected(resultFile.getParentFile(), expectedResultPath);
    }
}
