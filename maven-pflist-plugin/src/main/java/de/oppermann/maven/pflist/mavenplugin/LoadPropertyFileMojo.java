package de.oppermann.maven.pflist.mavenplugin;

import de.oppermann.maven.pflist.property.PropertyFileLoader;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

/**
 * User: sop
 * Date: 10.05.11
 * Time: 10:08
 *
 * @goal loadPropertyFile
 */
public class LoadPropertyFileMojo extends AbstractMojo {

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
     * @required
     */
    private String propertyFilePath;

    public void execute() throws MojoExecutionException, MojoFailureException {
        PropertyFileLoader pfl = new PropertyFileLoader();

        URL url = this.getClass().getClassLoader().getResource(propertyFilePath);

        Properties properties = pfl.loadProperties(url);

        project.getProperties().putAll(properties);
    }

}