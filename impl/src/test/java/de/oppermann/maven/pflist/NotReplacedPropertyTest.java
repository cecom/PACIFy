package de.oppermann.maven.pflist;

import de.oppermann.maven.pflist.commandline.CommandLineParameter;
import de.oppermann.maven.pflist.commandline.CommandLineUtils;
import de.oppermann.maven.pflist.defect.Defect;
import de.oppermann.maven.pflist.logger.LogLevel;
import de.oppermann.maven.pflist.property.PropertyFileProperties;
import de.oppermann.maven.pflist.utils.FileUtils;
import de.oppermann.maven.pflist.xml.PFManager;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.net.URL;
import java.util.EnumMap;
import java.util.List;

import static org.testng.AssertJUnit.assertTrue;

/**
 * User: sop
 * Date: 17.05.11
 * Time: 13:17
 */
public class NotReplacedPropertyTest extends CheckTest {

    @Test
    public void checkForNotCorrect() {
        File startPath = new File("target/test-classes/notReplacedPropertyTest");
        File myTestProperty = new File(startPath, "myProperties.properties");
        URL myTestPropertyURL = FileUtils.getFileUrl(myTestProperty);

        assertTrue("StartPath [" + startPath.getPath() + "] doesn't exist!", startPath.exists());

        EnumMap<CommandLineParameter, Object> commandlineProperties = new EnumMap<CommandLineParameter, Object>(CommandLineParameter.class);
        commandlineProperties.put(CommandLineParameter.StartPath, startPath);
        commandlineProperties.put(CommandLineParameter.PropertyFileURL, Util.getURLForFile(myTestProperty));
        commandlineProperties.put(CommandLineParameter.LogLevel, LogLevel.DEBUG);

        PFManager pfManager = new PFManager(startPath);
        List<Defect> defects = pfManager.doReplacement(new PropertyFileProperties(myTestPropertyURL));

         Assert.assertEquals(defects.size(), 4);
    }
}
