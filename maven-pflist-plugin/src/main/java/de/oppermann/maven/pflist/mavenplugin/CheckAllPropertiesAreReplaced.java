package de.oppermann.maven.pflist.mavenplugin;

import de.oppermann.maven.pflist.defect.Defect;
import de.oppermann.maven.pflist.logger.Log;
import de.oppermann.maven.pflist.logger.LogLevel;
import de.oppermann.maven.pflist.utils.Utils;
import de.oppermann.maven.pflist.model.PFEntityManager;
import de.oppermann.maven.pflist.model.PFFileEntity;
import de.oppermann.maven.pflist.model.PFListEntity;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * User: sop
 * Date: 18.05.11
 * Time: 12:52
 *
 * @goal checkAllPropertiesAreReplaced
 * @phase install
 */
public class CheckAllPropertiesAreReplaced extends AbstractMojo {

    /**
     * @parameter default-value="${project}"
     * @required
     * @readonly
     */
    protected MavenProject project;

    /**
     * @parameter default-value="${project.build.outputDirectory}"
     * @required
     */
    private File pfListStartPath;

    /**
     * Should it be skipped??
     *
     * @parameter expression="${pflist.skip}" default-value="false"
     */
    protected boolean skip;

    public void execute() throws MojoExecutionException {
        if (skip) {
            getLog().info("PFList is skipped. Nothing to do.");
            return;
        }

        if (!pfListStartPath.exists()) {
            File outputDirectory = new File(project.getModel().getBuild().getOutputDirectory());
            if (pfListStartPath.equals(outputDirectory))
                return; //if it is a maven project which doesn't have a target folder, do nothing.
            throw new MojoExecutionException("The folder [" + pfListStartPath.getAbsolutePath() + "] does not exist.");
        }

        Log.getInstance().setLogLevel(LogLevel.ERROR);

        PFEntityManager pfEntityManager = new PFEntityManager(pfListStartPath);
        if (pfEntityManager.getPFListCount() == 0) {
            getLog().info("No pflist files found. Nothing to check.");
            return;
        }
        getLog().info("Found [" + pfEntityManager.getPFListCount() + "] PFList Files...");

         getLog().info("Checking files...");
        List<Defect> defects = new ArrayList<Defect>();
        for (PFListEntity pfListEntity : pfEntityManager.getPFLists()) {
            for (PFFileEntity pfFileEntity : pfListEntity.getPfFileEntities()) {
                File file = pfListEntity.getAbsoluteFileFor(pfFileEntity);
                defects.addAll(Utils.checkFileForNotReplacedStuff(file));
            }
        }

        if (defects.isEmpty())
            return;

        getLog().error("==== !!!!!! We got Errors !!!!! ...");
        for (Defect defect : defects)
            getLog().error(defect.getDefectMessage());
        throw new MojoExecutionException("We got errors... Aborting!");
    }
}
