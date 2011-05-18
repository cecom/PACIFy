package de.oppermann.maven.pflist.mavenplugin;

import de.oppermann.maven.pflist.defect.Defect;
import de.oppermann.maven.pflist.utils.Utils;
import de.oppermann.maven.pflist.xml.PFFile;
import de.oppermann.maven.pflist.xml.PFList;
import de.oppermann.maven.pflist.xml.PFManager;
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
 * @goal check
 */
public class CheckMojo extends AbstractMojo {

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

        PFManager pfManager = new PFManager(pfListStartPath);
        if (pfManager.getPFListCount() == 0) {
            getLog().info("No pflist files found. Nothing to check.");
            return;
        }
        getLog().info("Found [" + pfManager.getPFListCount() + "] PFList Files...");


        List<Defect> defects = new ArrayList<Defect>();
        for (PFList pfList : pfManager.getPFLists()) {
            for (PFFile pfFile : pfList.getPfFiles()) {
                File file = pfList.getAbsoluteFileFor(pfFile);
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
