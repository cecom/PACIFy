package de.oppermann.maven.pflist;

import de.oppermann.maven.pflist.commandline.CommandLineParameter;
import de.oppermann.maven.pflist.commandline.CommandLineUtils;
import de.oppermann.maven.pflist.commandline.OutputType;
import de.oppermann.maven.pflist.logger.Log;
import de.oppermann.maven.pflist.logger.LogLevel;
import de.oppermann.maven.pflist.property.FilePropertyContainer;
import de.oppermann.maven.pflist.property.PropertyContainer;
import de.oppermann.maven.pflist.replacer.PropertyFileReplacer;
import de.oppermann.maven.pflist.utils.Utils;

import java.io.*;
import java.net.URL;
import java.util.EnumMap;
import java.util.Enumeration;

/**
 * User: sop
 * Date: 21.05.11
 * Time: 10:12
 */
public class CreateResultPropertyFile {
    EnumMap<CommandLineParameter, Object> commandlineProperties;

    /**
     * @param args --property_file=<path to property file>
     *             --targetFile=<where to write the result>
     *             [--logLevel]=<LogLevel>, defaults to LogLevel.Info
     *             [--help] print help
     * @see de.oppermann.maven.pflist.logger.LogLevel
     */
    public static void main(String[] args) {
        EnumMap<CommandLineParameter, Object> commandlineProperties = CommandLineUtils.getCommandLinePropertiesForCreateResultPropertyFile(args);

        if (commandlineProperties.containsKey(CommandLineParameter.Help) || commandlineProperties.isEmpty()) {
            CommandLineUtils.printCreateResultPropertyFileHelp();
            return;
        }

        CreateResultPropertyFile pfListPropertyReplacer = new CreateResultPropertyFile(commandlineProperties);
        pfListPropertyReplacer.create();
    }

    public CreateResultPropertyFile(EnumMap<CommandLineParameter, Object> commandlineProperties) {
        this.commandlineProperties = commandlineProperties;
        Log.getInstance().setLogLevel(getCommandLineLogLevel());
        Log.log(LogLevel.INFO, "== Executing CreateResultPropertyFile [Version=" + Utils.getJarVersion() + "]");
        Log.log(LogLevel.INFO, "     [LogLevel=" + getCommandLineLogLevel() + "]");
        Log.log(LogLevel.INFO, "     [PropertyFileURL=" + getPropertyFileURL().getPath() + "]");
        if (getOutputType() == OutputType.File)
            Log.log(LogLevel.INFO, "     [TargetFile=" + getTargetFile().getPath() + "]");
    }

    public void create() {
        PropertyContainer propertyContainer = new FilePropertyContainer(getPropertyFileURL());

        File tmpFile = createTempFile();

        //first, lets write all property to the target file
        PrintWriter out = null;
        try {
            out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(tmpFile), propertyContainer.getEncoding()), false);
            for (Enumeration e = propertyContainer.getProperties().propertyNames(); e.hasMoreElements(); ) {
                String propertyId = (String) e.nextElement();
                String propertyValue = propertyContainer.getPropertyValue(propertyId);

                //i don't use propertyContainer.getProperties.store(..) because he quotes
                out.println(propertyId + "=" + propertyValue);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (out != null) {
                out.close();
            }
        }

        //second, replace all in place used variables e.g. bla=%{foo}%{bar}
        PropertyFileReplacer replacer = new PropertyFileReplacer(propertyContainer);
        replacer.replace(tmpFile);

        if (getOutputType() == OutputType.File)
            tmpFile.renameTo(getTargetFile());
        else if (getOutputType() == OutputType.Stdout)
            writeToStdout(tmpFile, propertyContainer.getEncoding());
        else
            throw new IllegalArgumentException("OutputType not implemented! [" + getOutputType() + "]");
    }

    private void writeToStdout(File tmpFile, String encoding) {
        BufferedWriter bw = null;
        BufferedReader br = null;
        try {
            bw = new BufferedWriter(new OutputStreamWriter(System.out, encoding));
            br = new BufferedReader(new FileReader(tmpFile));

            for (String line; (line = br.readLine()) != null; ) {
                bw.write(line);
                bw.newLine();
            }
            bw.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                br.close();
                //you should not close bw because you close the maven stdout too
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

    private File createTempFile() {
        try {
            return File.createTempFile("pftmp", "properties");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private File getTargetFile() {
        return (File) commandlineProperties.get(CommandLineParameter.TargetFile);
    }

    private URL getPropertyFileURL() {
        return (URL) commandlineProperties.get(CommandLineParameter.PropertyFileURL);
    }

    private LogLevel getCommandLineLogLevel() {
        return (LogLevel) commandlineProperties.get(CommandLineParameter.LogLevel);
    }

    private OutputType getOutputType() {
        return (OutputType) commandlineProperties.get(CommandLineParameter.OutputType);
    }

}
