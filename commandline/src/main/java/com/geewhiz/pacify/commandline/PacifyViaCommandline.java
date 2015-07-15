package com.geewhiz.pacify.commandline;

import java.util.List;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.geewhiz.pacify.CreatePropertyFile;
import com.geewhiz.pacify.Replacer;
import com.geewhiz.pacify.ShowUsedProperties;
import com.geewhiz.pacify.Validator;
import com.geewhiz.pacify.commandline.commands.BasePropertyResolverCommand;
import com.geewhiz.pacify.commandline.commands.CreatePropertyFileCommand;
import com.geewhiz.pacify.commandline.commands.MainCommand;
import com.geewhiz.pacify.commandline.commands.ReplacerCommand;
import com.geewhiz.pacify.commandline.commands.ShowUsedPropertiesCommand;
import com.geewhiz.pacify.commandline.commands.ValidateCommand;
import com.geewhiz.pacify.commandline.commands.ValidateMarkerFilesCommand;
import com.geewhiz.pacify.resolver.PropertyResolverModule;
import com.geewhiz.pacify.utils.LoggingUtils;
import com.geewhiz.pacify.utils.Utils;
import com.google.inject.Guice;
import com.google.inject.Injector;

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

public class PacifyViaCommandline {

    private static Logger logger = LogManager.getLogger(PacifyViaCommandline.class.getName());

    public static void main(String... args) {
        System.err.println("PACIFy Version: " + Utils.getJarVersion());

        int resultValue = mainInternal(args);

        logger.debug("Exiting with exit code " + resultValue);
        System.exit(resultValue);
    }

    protected static int mainInternal(String[] args) {
        MainCommand mainCommand = new MainCommand();
        ReplacerCommand replacerCommand = new ReplacerCommand();
        CreatePropertyFileCommand createPropertyFileCommand = new CreatePropertyFileCommand();
        ValidateCommand validateCommand = new ValidateCommand();
        ValidateMarkerFilesCommand validateMarkerFilesCommand = new ValidateMarkerFilesCommand();
        ShowUsedPropertiesCommand showUsedPropertiesCommand = new ShowUsedPropertiesCommand();

        JCommander jc = new JCommander(mainCommand);
        jc.addCommand("replace", replacerCommand);
        jc.addCommand("createPropertyFile", createPropertyFileCommand);
        jc.addCommand("validate", validateCommand);
        jc.addCommand("validateMarkerFiles", validateMarkerFilesCommand);
        jc.addCommand("showUsedProperties", showUsedPropertiesCommand);

        try {
            jc.parse(args);
        } catch (ParameterException e) {
            System.err.println(e.getMessage());
            return 1;
        }

        if (mainCommand.isDebug()) {
            LoggingUtils.setLogLevel(logger, Level.DEBUG);
        } else if (mainCommand.isInfo()) {
            LoggingUtils.setLogLevel(logger, Level.INFO);
        } else {
            LoggingUtils.setLogLevel(logger, Level.ERROR);
        }

        if ("replace".equals(jc.getParsedCommand())) {
            return executeReplacer(replacerCommand);
        } else if ("createPropertyFile".equals(jc.getParsedCommand())) {
            return executeCreatePropertyFile(createPropertyFileCommand);
        } else if ("validate".equals(jc.getParsedCommand())) {
            return executeValidate(validateCommand);
        } else if ("validateMarkerFiles".equals(jc.getParsedCommand())) {
            return executeValidateMarkerFiles(validateMarkerFilesCommand);
        } else if ("showUsedProperties".equals(jc.getParsedCommand())) {
            return executeShowUsedProperties(showUsedPropertiesCommand);
        } else {
            jc.usage();
            if (mainCommand.isHelp()) {
                return 0;
            }
        }
        return 1;
    }

    private static int executeValidateMarkerFiles(ValidateMarkerFilesCommand validateMarkerFilesCommand) {
        Validator validator = new Validator(null);
        validateMarkerFilesCommand.configure(validator);
        validator.execute();

        return 0;
    }

    private static int executeValidate(ValidateCommand validateCommand) {
        Injector injector = getInjector(validateCommand);

        Validator validator = injector.getInstance(Validator.class);
        validateCommand.configure(validator);
        validator.execute();

        return 0;
    }

    private static int executeCreatePropertyFile(CreatePropertyFileCommand createPropertyFileCommand) {
        Injector injector = getInjector(createPropertyFileCommand);

        CreatePropertyFile createPropertyFile = injector.getInstance(CreatePropertyFile.class);
        createPropertyFileCommand.configure(createPropertyFile);
        createPropertyFile.writeTo();

        return 0;
    }

    private static int executeReplacer(ReplacerCommand replacerCommand) {
        Injector injector = getInjector(replacerCommand);

        Replacer replacer = injector.getInstance(Replacer.class);
        replacerCommand.configure(replacer);
        replacer.execute();

        return 0;
    }

    private static int executeShowUsedProperties(ShowUsedPropertiesCommand showUsedPropertiesCommand) {
        ShowUsedProperties showUsedProperties = new ShowUsedProperties();
        showUsedPropertiesCommand.configure(showUsedProperties);
        showUsedProperties.execute();

        return 0;
    }

    private static Injector getInjector(BasePropertyResolverCommand command) {
        List<PropertyResolverModule> propertyResolverModules = command.getPropertyResolverModules();

        Injector injector = Guice.createInjector(propertyResolverModules);
        return injector;
    }

}
