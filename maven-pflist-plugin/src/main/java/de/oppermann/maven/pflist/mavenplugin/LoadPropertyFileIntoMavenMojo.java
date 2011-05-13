package de.oppermann.maven.pflist.mavenplugin;

import de.oppermann.maven.pflist.property.PropertyFile;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

import java.net.URL;
import java.util.Properties;

/**
 * User: sop
 * Date: 10.05.11
 * Time: 10:08
 *
 * @goal loadPropertyFileIntoMaven
 */
public class LoadPropertyFileIntoMavenMojo extends AbstractMojo {

    /**
     * @parameter default-value="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    /**
     * Which property file should be used?
     *
     * @parameter
     */
    private String propertyFilePath;

    public void execute() throws MojoExecutionException, MojoFailureException {
        if (propertyFilePath == null) {
            getLog().info("No pflist property file given. Nothing to do.");
            return;
        }

        getLog().info("Loading pflist property from file [" + propertyFilePath + "]... ");

        URL propertyFileURL = this.getClass().getClassLoader().getResource(propertyFilePath);

        if (propertyFileURL == null)
            throw new MojoExecutionException("Couldn't find property file [" + propertyFilePath + "] ... Aborting!");

        PropertyFile propertyFile = new PropertyFile(propertyFileURL);
        Properties properties = propertyFile.getProperties();

        project.getProperties().putAll(properties);
    }

}