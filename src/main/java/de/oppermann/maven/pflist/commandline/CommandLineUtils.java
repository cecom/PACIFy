package de.oppermann.maven.pflist.commandline;

import de.oppermann.maven.pflist.logger.LogLevel;

import java.io.File;
import java.util.EnumMap;

/**
 * User: sop
 * Date: 03.05.11
 * Time: 13:05
 */

public class CommandLineUtils {

    public static EnumMap<CommandLineParameter, Object> getPropertiesFromParameter(String[] args) {
        EnumMap<CommandLineParameter, Object> commandlineProperties = new EnumMap<CommandLineParameter, Object>(CommandLineParameter.class);
        for (String string : args) {
            String key = string.split("=")[0];
            String value = string.split("=")[1];
            if (key.equals("--property_file")) {
                File propertyFile = new File(value);
                if (!propertyFile.exists() || propertyFile.isDirectory())
                    throw new RuntimeException("PropertyFile [" + value + "] does not exist or isn't a file... Aborting!");
                commandlineProperties.put(CommandLineParameter.PropertyFile, propertyFile);

            }
            if (key.equals("--path")) {
                File file = new File(value);
                if (!file.exists())
                    throw new IllegalArgumentException("StartPath [" + value + "] does not exist... Aborting!");
                if (!file.isDirectory())
                    throw new IllegalArgumentException("StartPath [" + value + "] is not a directory... Aborting!");
                commandlineProperties.put(CommandLineParameter.StartPath, file);
            }
            if (key.equals("--logLevel")) {
                LogLevel logLevel = LogLevel.valueOf(value);
                commandlineProperties.put(CommandLineParameter.LogLevel, logLevel);
            }
        }

        if (!commandlineProperties.containsKey(CommandLineParameter.PropertyFile))
            throw new IllegalArgumentException("[--property_file] is missing as parameter... Aborting!");

        if (!commandlineProperties.containsKey(CommandLineParameter.StartPath)) {
            File file = new File(".");
            commandlineProperties.put(CommandLineParameter.StartPath, file);
        }

        if (!commandlineProperties.containsKey(CommandLineParameter.LogLevel))
            commandlineProperties.put(CommandLineParameter.LogLevel, LogLevel.INFO);

        return commandlineProperties;
    }

}
