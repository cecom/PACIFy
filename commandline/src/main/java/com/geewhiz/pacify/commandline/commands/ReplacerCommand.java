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
import com.geewhiz.pacify.Replacer;



@Parameters(separators = "=", commandDescription = "Used to configure a package.")
public class ReplacerCommand extends BasePropertyResolverCommand {

    @Parameter(names = { "-p", "--packagePath" }, description = "The package path which you want to configure.", required = true)
    private File packagePath;

    @Parameter(names = { "-c", "--copyTo" }, description = "Create first a copy and configure the copy not the original package.", required = false)
    private File copyDestination;

    public void configure(Replacer replacer) {
        replacer.setPackagePath(packagePath);
        replacer.setCopyDestination(copyDestination);
    }
}
