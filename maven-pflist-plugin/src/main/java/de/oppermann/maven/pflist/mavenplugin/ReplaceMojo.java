package de.oppermann.maven.pflist.mavenplugin;

import de.oppermann.maven.pflist.model.PFEntityManager;
import de.oppermann.maven.pflist.property.FilePropertyContainer;
import de.oppermann.maven.pflist.property.MavenPropertyContainer;
import de.oppermann.maven.pflist.property.PropertyContainer;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.File;

/**
 * User: sop
 * Date: 13.05.11
 * Time: 10:29
 *
 * @goal replace
 * @phase generate-resources
 */
public class ReplaceMojo extends BaseMojo {

    /**
     * @parameter default-value="${project.build.outputDirectory}"
     * @required
     */
    private File pfListStartPath;

    /**
     * should we use the maven properties instead of a file?
     *
     * @parameter default-value="true"
     * @required
     */
    protected boolean useMavenProperties;

    /**
     * If you defined useMavenProperties with false, you have to define the propertyFile.
     *
     * @parameter expression="${pflist.usePropertyFile}"
     */
    protected String propertyFile;

    /**
     * If you defined useMavenProperties with false, you have to define in which jar is the propertyFile contained?
     *
     * @parameter
     */
    protected String propertyFileArtifact;

    @Override
    protected void executePFList() throws MojoExecutionException {
        if (!pfListStartPath.exists()) {
            File outputDirectory = new File(project.getModel().getBuild().getOutputDirectory());
            if (pfListStartPath.equals(outputDirectory)) {
                getLog().debug("Directory [" + pfListStartPath.getAbsolutePath() + "] does  not exists. Nothing to do.");
                return; //if it is a maven project which doesn't have a target folder, do nothing.
            }
            throw new MojoExecutionException("The folder [" + pfListStartPath.getAbsolutePath() + "] does not exist.");
        }

        PFEntityManager pfEntityManager = new PFEntityManager(pfListStartPath);
        if (pfEntityManager.getPFListCount() == 0) {
            getLog().info("No pflist files found. Nothing to do.");
            return;
        }
        getLog().info("Found [" + pfEntityManager.getPFListCount() + "] PFList Files...");

        PropertyContainer propertyContainer;
        if (useMavenProperties) {
            propertyContainer = new MavenPropertyContainer(project.getProperties());
        } else {
            propertyContainer = new FilePropertyContainer(getPropertyFileURL(propertyFileArtifact, propertyFile));
        }

        getLog().info("Loading properties from [" + propertyContainer.getPropertyLoadedFrom() + "]... ");
        getLog().info("Checking PFListFiles...");
        pfEntityManager.checkCorrectnessOfPFListFiles(propertyContainer);

        getLog().info("Doing Replacement...");
        pfEntityManager.doReplacement(propertyContainer);
    }

}

