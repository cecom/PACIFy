package de.oppermann.maven.pflist.mavenplugin;

import de.oppermann.maven.pflist.property.PropertyContainer;
import de.oppermann.maven.pflist.property.FilePropertyContainer;
import org.apache.maven.plugin.MojoExecutionException;

import java.net.URL;
import java.util.Properties;

/**
 * User: sop
 * Date: 10.05.11
 * Time: 10:08
 *
 * @goal loadPropertyFileIntoMaven
 * @phase validate
 */
public class LoadPropertyFileIntoMavenMojo extends BaseMojo {

     /**
     * In which jar is the propertyFile contained?
     *
     * @parameter
     * @required
     */
    protected String propertyFileArtifact;

    /**
     * Which property file should be used?
     *
     * @parameter expression="${pflist.usePropertyFile}"
     */
    protected String propertyFile;

    @Override
    protected void executePFList() throws MojoExecutionException {
        URL propertyFileURL = getPropertyFileURL(propertyFileArtifact,propertyFile);

        PropertyContainer propertyContainer = new FilePropertyContainer(propertyFileURL);

        getLog().info("Loading properties from [" + propertyContainer.getPropertyLoadedFrom() + "]...");

        Properties properties = propertyContainer.getProperties();
        project.getProperties().putAll(properties);
    }

}