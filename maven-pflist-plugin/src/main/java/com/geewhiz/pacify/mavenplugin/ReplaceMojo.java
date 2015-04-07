package com.geewhiz.pacify.mavenplugin;

import org.apache.maven.plugin.MojoExecutionException;

import com.geewhiz.pacify.defect.Defect;
import com.geewhiz.pacify.model.PFEntityManager;
import com.geewhiz.pacify.property.FilePropertyContainer;
import com.geewhiz.pacify.property.MavenPropertyContainer;
import com.geewhiz.pacify.property.PropertyContainer;

import java.io.File;
import java.util.List;

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
            propertyContainer = new MavenPropertyContainer(project.getProperties(), project.getModel().getModelEncoding());
        } else {
            propertyContainer = new FilePropertyContainer(getPropertyFileURL(propertyFileArtifact, propertyFile));
        }

        getLog().info("Loading properties from [" + propertyContainer.getPropertyLoadedFrom() + "]... ");
        getLog().info("Checking PFListFiles...");
        List<Defect> defects = pfEntityManager.checkCorrectnessOfPFListFiles(propertyContainer);
        checkDefects(defects);

        getLog().info("Doing Replacement...");
        defects = pfEntityManager.doReplacement(propertyContainer);
        checkDefects(defects);
    }

    private void checkDefects(List<Defect> defects) throws MojoExecutionException {
        if (defects.isEmpty())
            return;
        getLog().error("==== !!!!!! We got Errors !!!!! ...");
        for (Defect defect : defects)
            getLog().error(defect.getDefectMessage());
        throw new MojoExecutionException("We got errors... Aborting!");
    }

}

