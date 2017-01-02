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



import com.beust.jcommander.Parameter;

public class MainCommand {

    @Parameter(names = "--help", help = true, description = "Used to print this information.")
    private boolean help;

    @Parameter(names = "--debug", description = "Debug mode")
    private boolean debug = false;

    @Parameter(names = "--info", description = "Info mode")
    private boolean info  = false;

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public boolean isInfo() {
        return info;
    }

    public void setInfo(boolean info) {
        this.info = info;
    }

    public boolean isHelp() {
        return help;
    }

}
