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

import static org.hamcrest.Matchers.is;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;
import org.junit.Assert;
import org.junit.Test;

import com.geewhiz.pacify.test.ListAppender;
import com.geewhiz.pacify.test.RegexMatcher;
import com.geewhiz.pacify.test.TestUtil;

public class TestPreConfigure {

    @Test
    public void testPreConfigure() throws IOException {
        File testResourceFolder = new File("src/test/resources/testPreConfigure");
        File targetResourceFolder = new File("target/test-resources/testPreConfigure");

        TestUtil.removeOldTestResourcesAndCopyAgain(testResourceFolder, targetResourceFolder);

        File myTestProperty = new File(targetResourceFolder, "properties/myTest.properties");
        File myPackagePath = new File(targetResourceFolder, "package");
        File myExpectedResultPath = new File(targetResourceFolder, "expectedResult");

        ListAppender listAppender = TestUtil.addListAppenderToLogger();

        PacifyViaCommandline pacifyViaCommandline = new PacifyViaCommandline();

        int result = pacifyViaCommandline.mainInternal(new String[] {
                "--info",
                "preConfigure",
                "--resolvers=FileResolver",
                "--packagePath=" + myPackagePath.getAbsolutePath(),
                "-RFileResolver.file=" + myTestProperty.getAbsolutePath() });

        Assert.assertEquals("Configuration returned with errors.", 0, result);
        TestUtil.checkIfResultIsAsExpected(myPackagePath, myExpectedResultPath);

        String[] output = listAppender.getLogMessages().toArray(new String[0]);
        String[] expect = {
                ".*== Executing PreConfigure \\[Version=.*",
                ".*commandline/target/test-resources/testPreConfigure/package\\]",
                "== Found \\[3\\] pacify marker files",
                "== Validating...",
                ".*commandline/target/test-resources/testPreConfigure/package/parent-CMFile.pacify\\]",
                ".*commandline/target/test-resources/testPreConfigure/package/folder/folder1-CMFile.pacify\\]",
                ".*commandline/target/test-resources/testPreConfigure/package/folder/folder2-CMFile.pacify\\]",
                "== Replacing...",
                ".*commandline/target/test-resources/testPreConfigure/package/parent-CMFile.pacify\\]",
                "      Customize File \\[someParentConf.conf\\]",
                "          \\[2\\] placeholders replaced.",
                ".*commandline/target/test-resources/testPreConfigure/package/folder/folder1-CMFile.pacify\\]",
                "      Customize File \\[someFolderConf.conf\\]",
                "          \\[1\\] placeholders replaced.",
                "      Customize File \\[subfolder/someSubFolderConf.conf\\]",
                "          \\[0\\] placeholders replaced.",
                ".*commandline/target/test-resources/testPreConfigure/package/folder/folder2-CMFile.pacify\\]",
                "      Customize File \\[anotherFolderConf.conf\\]",
                "          \\[1\\] placeholders replaced.",
                "== Properties which are not resolved \\[2\\] ...",
                "   foo",
                "   foobar3",
                "== Successfully finished"
        };

        Assert.assertThat(output.length, is(expect.length));

        for (int i = 0; i < output.length; i++) {
            String outputLine = FilenameUtils.separatorsToUnix(output[i]);
            String expectedLine = expect[i];
            Assert.assertThat(outputLine, RegexMatcher.matchesRegex(expectedLine));
        }
    }
}
