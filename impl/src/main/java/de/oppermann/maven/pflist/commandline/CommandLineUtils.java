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
            String[] split = string.split("=");
            String key = split[0];
            String value = split.length > 1 ? split[1] : null;
            if (key.equals("--propertyFile")) {
                File propertyFile = new File(value);
                if (!propertyFile.exists() || propertyFile.isDirectory())
                    throw new RuntimeException("PropertyFile [" + value + "] does not exist or isn't a file... Aborting!");
                commandlineProperties.put(CommandLineParameter.PropertyFile, propertyFile);
            }
            if (key.equals("--startPath")) {
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

            if (key.equals("--help")) {
                commandlineProperties.put(CommandLineParameter.Help, true);
            }
        }

        if (commandlineProperties.containsKey(CommandLineParameter.Help) || commandlineProperties.isEmpty())
            return commandlineProperties;

        if (!commandlineProperties.containsKey(CommandLineParameter.PropertyFile)) {
            printHelp();
            throw new IllegalArgumentException("[--propertyFile] is missing as parameter... Aborting!");
        }

        if (!commandlineProperties.containsKey(CommandLineParameter.StartPath)) {
            File file = new File(".");
            commandlineProperties.put(CommandLineParameter.StartPath, file);
        }

        if (!commandlineProperties.containsKey(CommandLineParameter.LogLevel))
            commandlineProperties.put(CommandLineParameter.LogLevel, LogLevel.INFO);

        return commandlineProperties;
    }

    public static final void printHelp() {
        System.out.println("Parameters:");
        System.out.println(" --propertyFile=<path>   -> Which property file should be used for filtering.");
        System.out.println(" [--startPath]=<path>    -> The folder where we are looking recursively for *-PFList.xml files. If not set, using current folder.");
        System.out.println(" [--logLevel]=<level>    -> The logLevel (DEBUG, INFO, ERROR). Default is INFO");
        System.out.println(" [--help]                -> This info.");
    }

}
