package de.oppermann.maven.pflist.mavenplugin;

import de.oppermann.maven.pflist.CreateResultPropertyFile;
import de.oppermann.maven.pflist.commandline.CommandLineParameter;
import de.oppermann.maven.pflist.logger.LogLevel;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.File;
import java.util.EnumMap;

/**
 * @goal generateFinalPropertyFile
 * @phase process-resources
 */
public class GenerateFinalPropertyFile extends BaseMojo {

    /**
     * @parameter default-value="${project.build.outputDirectory}"
     * @required
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
        if (!outputDirectory.exists()) {
            outputDirectory.mkdirs();
        }

        for (String propertyFile : propertyFiles.split(",")) {
            File targetFile = new File(outputDirectory, propertyFile);
            getLog().info("Creating final property file [" + targetFile.getPath() + "] ...");

            EnumMap<CommandLineParameter, Object> commandlineProperties = new EnumMap<CommandLineParameter, Object>(CommandLineParameter.class);
            commandlineProperties.put(CommandLineParameter.PropertyFileURL, getPropertyFileURL(propertyFileArtifact, propertyFile));
            commandlineProperties.put(CommandLineParameter.LogLevel, LogLevel.ERROR);
            commandlineProperties.put(CommandLineParameter.TargetFile, targetFile);

            CreateResultPropertyFile createResultPropertyFile = new CreateResultPropertyFile(commandlineProperties);
            createResultPropertyFile.create();
        }
    }
}