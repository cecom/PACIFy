package de.oppermann.maven.pflist.mavenplugin;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

import java.net.URL;

/**
 * User: sop
 * Date: 14.05.11
 * Time: 11:07
 */
public abstract class BaseMojo extends AbstractMojo {

    /**
     * @parameter default-value="${project}"
     * @required
     * @readonly
     */
    protected MavenProject project;

    /**
     * Which property file should be used?
     *
     * @parameter expression="${pflist.usePropertyFile}"
     */
    protected String propertyFilePath;

    /**
     * Which property file should be used?
     *
     * @parameter expression="${pflist.skip}" default-value="false"
     */
    protected boolean skip;


    public void execute() throws MojoExecutionException, MojoFailureException {
        if(skip){
            getLog().info("PFList is skipped. Nothing to do.");
            return;
        }
        executePFList();
    }

    protected abstract void executePFList() throws MojoExecutionException;

    protected URL getPropertyFileURL() throws MojoExecutionException {
        if (propertyFilePath == null) {
            getLog().info("No pflist property file given. Nothing to do.");
            return null;
        }

        URL propertyFileURL = this.getClass().getClassLoader().getResource(propertyFilePath);

        if (propertyFileURL == null)
            throw new MojoExecutionException("Couldn't find property file [" + propertyFilePath + "] ... Aborting!");

        getLog().info("Loading pflist property from [" + propertyFileURL.getPath() + "]... ");

        return propertyFileURL;
    }


}
