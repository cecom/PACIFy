package de.oppermann.maven.pflist.mavenplugin;

import de.oppermann.maven.pflist.property.PropertyFileProperties;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.net.URL;
import java.util.Properties;

/**
 * User: sop
 * Date: 10.05.11
 * Time: 10:08
 *
 * @goal loadPropertyFileIntoMaven
 */
public class LoadPropertyFileIntoMavenMojo extends BaseMojo {

    @Override
    protected void executePFList() throws MojoExecutionException {
        URL propertyFileURL = getPropertyFileURL();

        PropertyFileProperties propertyFileProperties = new PropertyFileProperties(propertyFileURL);
        Properties properties = propertyFileProperties.getProperties();

        project.getProperties().putAll(properties);
    }

}