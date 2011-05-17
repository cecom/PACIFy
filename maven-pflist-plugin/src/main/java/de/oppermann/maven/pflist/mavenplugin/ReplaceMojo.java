package de.oppermann.maven.pflist.mavenplugin;

import de.oppermann.maven.pflist.property.PropertyFileProperties;
import de.oppermann.maven.pflist.xml.PFManager;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.File;
import java.net.URL;

/**
 * User: sop
 * Date: 13.05.11
 * Time: 10:29
 *
 * @goal replace
 */
public class ReplaceMojo extends BaseMojo {

    /**
     * @parameter default-value="${project.build.outputDirectory}"
     * @required
     */
    private File pfListStartPath;

    @Override
    protected void executePFList() throws MojoExecutionException {
        if (!pfListStartPath.exists()) {
            File outputDirectory = new File(project.getModel().getBuild().getOutputDirectory());
            if (pfListStartPath.equals(outputDirectory))
                return; //if it is a maven project which doesn't have a target folder, do nothing.
            throw new MojoExecutionException("The folder [" + pfListStartPath.getAbsolutePath() + "] does not exist.");
        }

        PFManager pfManager = new PFManager(pfListStartPath);
        if (pfManager.getPFListCount() == 0) {
            getLog().info("No pflist files found. Nothing to do.");
            return;
        }
        getLog().info("Found [" + pfManager.getPFListCount() + "] PFList Files...");

        PropertyFileProperties propertyFileProperties = new PropertyFileProperties(getPropertyFileURL());

        getLog().info("Checking PFListFiles...");
        pfManager.checkCorrectnessOfPFListFiles(propertyFileProperties);

        getLog().info("Doing Replacement...");
        pfManager.doReplacement(propertyFileProperties);
    }

}

