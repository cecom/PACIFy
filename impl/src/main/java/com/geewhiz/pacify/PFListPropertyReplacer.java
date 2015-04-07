package com.geewhiz.pacify;


import java.io.File;
import java.net.URL;
import java.util.EnumMap;
import java.util.List;

import com.geewhiz.pacify.commandline.CommandLineParameter;
import com.geewhiz.pacify.commandline.CommandLineUtils;
import com.geewhiz.pacify.defect.Defect;
import com.geewhiz.pacify.logger.Log;
import com.geewhiz.pacify.logger.LogLevel;
import com.geewhiz.pacify.model.PFEntityManager;
import com.geewhiz.pacify.property.FilePropertyContainer;
import com.geewhiz.pacify.property.PropertyContainer;
import com.geewhiz.pacify.utils.Utils;

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
     *             [--help] print help
     * @see LogLevel
     */
    public static void main(String[] args) {
        EnumMap<CommandLineParameter, Object> commandlineProperties = CommandLineUtils.getCommandLinePropertiesForPropertyReplacer(args);

        if (commandlineProperties.containsKey(CommandLineParameter.Help) || commandlineProperties.isEmpty()) {
            CommandLineUtils.printPropertyReplacerHelp();
            return;
        }

        PFListPropertyReplacer pfListPropertyReplacer = new PFListPropertyReplacer(commandlineProperties);
        pfListPropertyReplacer.replace();
    }

    public PFListPropertyReplacer(EnumMap<CommandLineParameter, Object> commandlineProperties) {
        this.commandlineProperties = commandlineProperties;
        Log.getInstance().setLogLevel(getCommandLineLogLevel());
        Log.log(LogLevel.INFO, "== Executing PFListPropertyReplacer [Version=" + Utils.getJarVersion() + "]");
        Log.log(LogLevel.INFO, "     [LogLevel=" + getCommandLineLogLevel() + "]");
        Log.log(LogLevel.INFO, "     [StartPath=" + getCommandLineStartPath().getAbsolutePath() + "]");
        Log.log(LogLevel.INFO, "     [PropertyFileURL=" + getCommandLinePropertyFileURL().getPath() + "]");
    }

    public void replace() {
        PFEntityManager pfEntityManager = new PFEntityManager(getCommandLineStartPath());

        Log.log(LogLevel.INFO, "==== Found [" + pfEntityManager.getPFListCount() + "] PFList Files...");

        Log.log(LogLevel.INFO, "==== Checking PFListFiles...");
        List<Defect> defects = pfEntityManager.checkCorrectnessOfPFListFiles(getPropertyFile());
        shouldWeAbortIt(defects);

        Log.log(LogLevel.INFO, "==== Doing Replacement...");
        defects = pfEntityManager.doReplacement(getPropertyFile());
        shouldWeAbortIt(defects);

        Log.log(LogLevel.INFO, "== Successfully finished...");
    }

    private File getCommandLineStartPath() {
        return (File) commandlineProperties.get(CommandLineParameter.StartPath);
    }

    private LogLevel getCommandLineLogLevel() {
        return (LogLevel) commandlineProperties.get(CommandLineParameter.LogLevel);
    }

    private PropertyContainer getPropertyFile() {
        return new FilePropertyContainer(getCommandLinePropertyFileURL());
    }

    private URL getCommandLinePropertyFileURL() {
        return (URL) commandlineProperties.get(CommandLineParameter.PropertyFileURL);
    }

    private void shouldWeAbortIt(List<Defect> defects) {
        if (defects.isEmpty())
            return;

        Log.log(LogLevel.ERROR, "==== !!!!!! We got Errors !!!!! ...");
        for (Defect defect : defects)
            Log.log(LogLevel.ERROR, defect.getDefectMessage());
        throw new RuntimeException("We got errors... Aborting!");
    }
}