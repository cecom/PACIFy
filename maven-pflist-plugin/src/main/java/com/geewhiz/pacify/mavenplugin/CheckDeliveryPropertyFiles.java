package com.geewhiz.pacify.mavenplugin;

import org.apache.maven.plugin.MojoExecutionException;

import com.geewhiz.pacify.checker.CheckPropertyDuplicateInPropertyFile;
import com.geewhiz.pacify.checker.CheckPropertyExists;
import com.geewhiz.pacify.defect.Defect;
import com.geewhiz.pacify.model.PFEntityManager;
import com.geewhiz.pacify.model.PFListEntity;
import com.geewhiz.pacify.property.FilePropertyContainer;
import com.geewhiz.pacify.property.PropertyContainer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * User: sop
 * Date: 19.05.11
 * Time: 09:13
 *
 * @goal checkDeliveryProperties
 * @phase install
 */
public class CheckDeliveryPropertyFiles extends BaseMojo {

    /**
     * @parameter default-value="${project.build.outputDirectory}"
     * @required
     */
    private File pfListStartPath;

    /**
     * which files should be checked? its a comma separated list
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
            getLog().info("No pflist files found. Nothing to check.");
            return;
        }

        getLog().info("Found [" + pfEntityManager.getPFListCount() + "] PFList Files...");

        List<Defect> defects = new ArrayList<Defect>();
        for (String propertyFile : propertyFiles.split(",")) {
            getLog().info("Checking property file [" + propertyFile + "] ...");
            defects.addAll(checkPropertyFile(pfEntityManager, propertyFile));
        }

        if (defects.isEmpty())
            return;

        getLog().error("==== !!!!!! We got Errors !!!!! ...");
        for (Defect defect : defects)
            getLog().error(defect.getDefectMessage());
        throw new MojoExecutionException("We got errors... Aborting!");
    }

    private List<Defect> checkPropertyFile(PFEntityManager pfEntityManager, String propertyFile) throws MojoExecutionException {
        PropertyContainer propertyContainer = new FilePropertyContainer(getPropertyFileURL(propertyFileArtifact, propertyFile));

        CheckPropertyDuplicateInPropertyFile duplicateChecker = new CheckPropertyDuplicateInPropertyFile(propertyContainer);
        CheckPropertyExists propertyExistsChecker = new CheckPropertyExists(propertyContainer);

        List<Defect> defects = new ArrayList<Defect>();
        defects.addAll(duplicateChecker.checkForErrors());

        for (PFListEntity pfListEntity : pfEntityManager.getPFLists())
            defects.addAll(propertyExistsChecker.checkForErrors(pfListEntity));

        return defects;
    }
}
