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

package com.geewhiz.pacify.commandline.commands;

import java.io.File;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.geewhiz.pacify.CreatePropertyFile;



@Parameters(separators = "=", commandDescription = "Used to resolve the property file with its dependencies")
public class CreatePropertyFileCommand extends BasePropertyResolverCommand {

    @Parameter(names = { "-d", "--destinationFile" }, description = "Where to write the result to. If not given, it will be printed to stdout", required = false)
    private File   targetFile;

    @Parameter(names = { "-e", "--targetEncoding" }, description = "Which encoding do you want in the created file", required = false)
    private String targetEncoding = "utf-8";

    @Parameter(names = { "-m", "--fileMode" }, description = "If you write the properties to a file, which filemode should the file have?", required = false)
    private String fileMode       = "400";

    @Parameter(names = { "-op", "--outputPrefix" }, description = "If you want to prefix every line on the output, set this value. Useful if you have to parse the output.", required = false)
    private String outputPrefix   = "";

    public void configure(CreatePropertyFile createPropertyFile) {
        createPropertyFile.setTargetFile(targetFile);
        createPropertyFile.setOutputEncoding(targetEncoding);
        createPropertyFile.setOutputType(targetFile != null ? CreatePropertyFile.OutputType.File
                : CreatePropertyFile.OutputType.Stdout);
        createPropertyFile.setFilemode(fileMode);
        createPropertyFile.setOutputPrefix(outputPrefix);
    }
}
