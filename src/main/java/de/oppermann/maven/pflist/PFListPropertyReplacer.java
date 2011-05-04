package de.oppermann.maven.pflist;

import de.oppermann.maven.pflist.commandline.CommandLineParameter;
import de.oppermann.maven.pflist.commandline.CommandLineUtils;
import de.oppermann.maven.pflist.logger.Log;
import de.oppermann.maven.pflist.logger.LogLevel;
import de.oppermann.maven.pflist.utils.Utils;
import de.oppermann.maven.pflist.xml.PFManager;

import java.io.File;
import java.util.EnumMap;

/**
 * User: sop
 * Date: 03.05.11
 * Time: 13:05
 */

public class PFListPropertyReplacer {

    EnumMap<CommandLineParameter, Object> commandlineProperties;

    /**
     * @param args the PFProperty PFFile which will be used for replacement
     *             --property_file=<path to property file>
     *             [--path]=<path where to start the replacement, if not given the current testAll.folder is used>
     *             [--logLevel]=<LogLevel>, defaults to LogLevel.Info
     * @see LogLevel
     */
    public static void main(String[] args) {
        PFListPropertyReplacer pfListPropertyReplacer = new PFListPropertyReplacer(CommandLineUtils.getPropertiesFromParameter(args));
        pfListPropertyReplacer.replace();
    }

    public PFListPropertyReplacer(EnumMap<CommandLineParameter, Object> commandlineProperties) {
        this.commandlineProperties = commandlineProperties;
        Log.getInstance().setLogLevel(getCommandLineLogLevel());
        Log.log(LogLevel.INFO, "== Executing PFListPropertyReplacer [Version=" + Utils.getJarVersion() + "]");
        Log.log(LogLevel.INFO, "     [LogLevel=" + getCommandLineLogLevel() + "]");
        Log.log(LogLevel.INFO, "     [StartPath=" + getCommandLineStartPath().getAbsolutePath() + "]");
        Log.log(LogLevel.INFO, "     [PropertyFile=" + getCommandLinePropertyFile().getAbsolutePath() + "]");
    }

    public void replace() {
        PFManager pfManager = new PFManager(getCommandLineStartPath(), getCommandLinePropertyFile());

        Log.log(LogLevel.INFO, "==== Found [" + pfManager.getPFListCount() + "] PFList Files...");

        Log.log(LogLevel.INFO, "==== Checking PFListFiles...");
        pfManager.checkCorrectnessOfPFListFiles();

        Log.log(LogLevel.INFO, "==== Doing Replacement...");
        pfManager.doReplacement();

        Log.log(LogLevel.INFO, "== Successfully finished...");
    }

    private File getCommandLineStartPath() {
        return (File) commandlineProperties.get(CommandLineParameter.StartPath);
    }

    private LogLevel getCommandLineLogLevel() {
        return (LogLevel) commandlineProperties.get(CommandLineParameter.LogLevel);
    }

    private File getCommandLinePropertyFile() {
        return (File) commandlineProperties.get(CommandLineParameter.PropertyFile);
    }
}
