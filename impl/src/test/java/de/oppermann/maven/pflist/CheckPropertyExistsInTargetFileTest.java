package de.oppermann.maven.pflist;

import de.oppermann.maven.pflist.checker.CheckPropertyExistsInTargetFile;
import de.oppermann.maven.pflist.defect.Defect;
import de.oppermann.maven.pflist.property.PropertyFileProperties;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.net.URL;
import java.util.List;

/**
 * User: sop
 * Date: 03.05.11
 * Time: 13:05
 */
public class CheckPropertyExistsInTargetFileTest extends CheckTest {
    @Test
    public void checkForNotCorrect() {
        File testStartPath = new File("target/test-classes/checkPropertyExistsInTargetFileTest/wrong");
        File file = new File(testStartPath, "myProperties.properties");

        URL fileUrl = Util.getURLForFile(file);
        PropertyFileProperties propertyFileProperties = new PropertyFileProperties(fileUrl);

        List<Defect> defects = getDefects(new CheckPropertyExistsInTargetFile(), testStartPath, propertyFileProperties);

        Assert.assertEquals(2, defects.size());
    }

    @Test
    public void checkForCorrect() {
        File testStartPath = new File("target/test-classes/checkPropertyExistsInTargetFileTest/correct");
        File file = new File(testStartPath, "myProperties.properties");

        URL fileUrl = Util.getURLForFile(file);
        PropertyFileProperties propertyFileProperties = new PropertyFileProperties(fileUrl);

        List<Defect> defects = getDefects(new CheckPropertyExistsInTargetFile(), testStartPath, propertyFileProperties);

        Assert.assertEquals(0, defects.size());
    }
}
