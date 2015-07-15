package com.geewhiz.pacify.commandline.commands;

import java.io.File;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.geewhiz.pacify.ShowUsedProperties;

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

@Parameters(separators = "=", commandDescription = "Used to show which properties will be used within the pacify marker files without resolving them.")
public class ShowUsedPropertiesCommand {

    @Parameter(names = { "-p", "--packagePath" }, description = "The package path.", required = true)
    public File    packagePath;

    @Parameter(names = { "-d", "--destinationFile" }, description = "Where to write the result to. If not given, it will be printed to stdout", required = false)
    private File   targetFile;

    @Parameter(names = { "-e", "--targetEncoding" }, description = "Which encoding do you want in the created file", required = false)
    private String targetEncoding = "utf-8";

    @Parameter(names = { "-op", "--outputPrefix" }, description = "If you want to prefix every line on the output, set this value. Useful if you have to parse the output.", required = false)
    private String outputPrefix   = "";

    public void configure(ShowUsedProperties showUsedProperties) {
        showUsedProperties.setPackagePath(packagePath);
        showUsedProperties.setTargetFile(targetFile);
        showUsedProperties.setOutputEncoding(targetEncoding);
        showUsedProperties.setOutputType(targetFile != null ? ShowUsedProperties.OutputType.File
                : ShowUsedProperties.OutputType.Stdout);
        showUsedProperties.setOutputPrefix(outputPrefix);

    }
}
