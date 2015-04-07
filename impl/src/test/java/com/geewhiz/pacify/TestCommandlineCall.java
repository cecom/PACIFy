package com.geewhiz.pacify;

import org.testng.annotations.Test;

import com.geewhiz.pacify.PFListPropertyReplacer;
import com.geewhiz.pacify.commandline.CommandLineParameter;
import com.geewhiz.pacify.logger.LogLevel;

import java.io.File;
import java.util.EnumMap;

import static org.testng.AssertJUnit.assertTrue;

/**
 * User: sop
 * Date: 03.05.11
 * Time: 13:05
 */
public class TestCommandlineCall extends BaseCheck {

    @Test
    public void testAll() {
        File startPath = new File("target/test-classes/testAll");
        File myTestProperty = new File(startPath, "myTest.properties");

        assertTrue("StartPath [" + startPath.getPath() + "] doesn't exist!", startPath.exists());

        EnumMap<CommandLineParameter, Object> commandlineProperties = new EnumMap<CommandLineParameter, Object>(CommandLineParameter.class);
        commandlineProperties.put(CommandLineParameter.StartPath, startPath);
        commandlineProperties.put(CommandLineParameter.PropertyFileURL, Util.getURLForFile(myTestProperty));
        commandlineProperties.put(CommandLineParameter.LogLevel, LogLevel.DEBUG);

        PFListPropertyReplacer pfListPropertyReplacer = new PFListPropertyReplacer(commandlineProperties);
        pfListPropertyReplacer.replace();

        Util.checkIfResultIsAsExpected(startPath);
    }
}
