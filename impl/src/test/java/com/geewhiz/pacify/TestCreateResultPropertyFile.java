package com.geewhiz.pacify;

import org.apache.tools.ant.IntrospectionHelper;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.geewhiz.pacify.CreateResultPropertyFile;
import com.geewhiz.pacify.commandline.CommandLineParameter;
import com.geewhiz.pacify.commandline.OutputType;
import com.geewhiz.pacify.defect.Defect;
import com.geewhiz.pacify.logger.LogLevel;
import com.geewhiz.pacify.model.PFEntityManager;
import com.geewhiz.pacify.property.FilePropertyContainer;
import com.geewhiz.pacify.utils.FileUtils;

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
