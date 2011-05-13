package de.oppermann.maven.pflist.mavenplugin;

import de.oppermann.maven.pflist.xml.PFManager;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

import java.io.File;

/**
 * User: sop
 * Date: 13.05.11
 * Time: 10:29
 *
 * @goal replace
 */
public class ReplaceMojo extends AbstractMojo {

    /**
     * @parameter default-value="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    /**
     * @parameter default-value="${project.build.outputDirectory}"
     * @required
     */
    private File pfListStartPath;

    public void execute() throws MojoExecutionException, MojoFailureException {
//        PFManager pfManager = new PFManager(pfListStartPath, );
//
//        if (pfManager.getPFListCount() == 0) {
//            getLog().info("No pflist files found. Nothing to do.");
//            return;
//        }
//        getLog().info("Found [" + pfManager.getPFListCount() + "] PFList Files...");
//
//        getLog().info("Checking PFListFiles...");
//        pfManager.checkCorrectnessOfPFListFiles();
//
//        getLog().info("Doing Replacement...");
//        pfManager.doReplacement();

//        URL url = this.getClass().getClassLoader().getResource(propertyFilePath);
//
//        if (url == null)
//            throw new MojoExecutionException("Couldn't find property file [" + propertyFilePath + "] ... Aborting!");
//
//        Properties properties = pfl.loadProperties(url);
//
//        project.getLocalProperties().putAll(properties);
    }

}

