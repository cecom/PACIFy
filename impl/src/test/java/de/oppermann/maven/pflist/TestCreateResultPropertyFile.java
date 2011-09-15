package de.oppermann.maven.pflist;

import de.oppermann.maven.pflist.commandline.CommandLineParameter;
import de.oppermann.maven.pflist.commandline.OutputType;
import de.oppermann.maven.pflist.defect.Defect;
import de.oppermann.maven.pflist.logger.LogLevel;
import de.oppermann.maven.pflist.model.PFEntityManager;
import de.oppermann.maven.pflist.property.FilePropertyContainer;
import de.oppermann.maven.pflist.utils.FileUtils;
import org.apache.tools.ant.IntrospectionHelper;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.net.URL;
import java.util.EnumMap;
import java.util.List;

import static org.testng.AssertJUnit.assertTrue;

/**
 * User: sop
 * Date: 21.05.11
 * Time: 11:33
 */
public class TestCreateResultPropertyFile {

    @Test
    public void checkForNotCorrect() {
        File startPath = new File("target/test-classes/TestCreateResultPropertyFile");

        File myTestProperty = new File(startPath, "subfolder/ChildOfChilds.properties");
        File targetFile = new File(startPath, "result.properties");

        assertTrue("StartPath [" + startPath.getPath() + "] doesn't exist!", startPath.exists());

        EnumMap<CommandLineParameter, Object> commandlineProperties = new EnumMap<CommandLineParameter, Object>(CommandLineParameter.class);
        commandlineProperties.put(CommandLineParameter.PropertyFileURL, Util.getURLForFile(myTestProperty));
        commandlineProperties.put(CommandLineParameter.LogLevel, LogLevel.DEBUG);
        commandlineProperties.put(CommandLineParameter.OutputType, OutputType.File);
        commandlineProperties.put(CommandLineParameter.TargetFile, targetFile);

        CreateResultPropertyFile createResultPropertyFile = new CreateResultPropertyFile(commandlineProperties);
        createResultPropertyFile.create();

        Util.checkIfResultIsAsExpected(startPath);
    }
}
