package com.geewhiz.pacify.mavenplugin;

import org.apache.maven.plugin.MojoExecutionException;

import com.geewhiz.pacify.CreateResultPropertyFile;
import com.geewhiz.pacify.commandline.CommandLineParameter;
import com.geewhiz.pacify.commandline.OutputType;
import com.geewhiz.pacify.logger.LogLevel;

import java.io.File;
import java.util.EnumMap;

/**
 * @goal generateFinalPropertyFile
 * @phase process-resources
 */
public class GenerateFinalPropertyFile extends BaseMojo {

    /**
     * if given, the property files will be written to this directory. if not given it will be written to stdout
     * @parameter"
     */
    private File outputDirectory;

    /**
     * which files should be generated? its a comma separated list
     *
     * @parameter
     * @required
     */
    private String propertyFiles;

    /**
     * In which jar is the propertyFile contained?
     *
     * @parameter
     * @required
     */
    protected String propertyFileArtifact;


    @Override
    protected void executePFList() throws MojoExecutionException {
        for (String propertyFile : propertyFiles.split(",")) {
            EnumMap<CommandLineParameter, Object> commandlineProperties = new EnumMap<CommandLineParameter, Object>(CommandLineParameter.class);
            commandlineProperties.put(CommandLineParameter.PropertyFileURL, getPropertyFileURL(propertyFileArtifact, propertyFile));
            commandlineProperties.put(CommandLineParameter.LogLevel, getLogLevel());

            if (outputDirectory != null) {
                if (!outputDirectory.exists()) {
                    outputDirectory.mkdirs();
                }
                File targetFile = new File(outputDirectory, propertyFile);
                getLog().info("Creating final property file [" + targetFile.getPath() + "] ...");
                commandlineProperties.put(CommandLineParameter.OutputType, OutputType.File);
                commandlineProperties.put(CommandLineParameter.TargetFile, targetFile);
            } else {
                getLog().info("Creating final property file [" + propertyFile + "] ...");
                commandlineProperties.put(CommandLineParameter.OutputType, OutputType.Stdout);
            }

            CreateResultPropertyFile createResultPropertyFile = new CreateResultPropertyFile(commandlineProperties);
            createResultPropertyFile.create();
        }
    }
}