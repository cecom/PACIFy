package de.oppermann.maven.pflist;

import de.oppermann.maven.pflist.checker.CheckPropertyExists;
import de.oppermann.maven.pflist.defect.Defect;
import de.oppermann.maven.pflist.property.PropertyFileProperties;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.net.URL;
import java.util.List;

/**
 * User: sop
 * Date: 03.05.11
 * Time: 13:05
 */
public class CheckPropertyExistsTest extends CheckTest {

    @Test
    public void checkForNotCorrect() {
        File testStartPath = new File("target/test-classes/checkPropertyExistsTest/wrong");
        File file = new File(testStartPath, "checkForMissingProperty.properties");

        URL fileUrl = Util.getURLForFile(file);
        PropertyFileProperties propertyFileProperties = new PropertyFileProperties(fileUrl);

        List<Defect> defects = getDefects(new CheckPropertyExists(propertyFileProperties), testStartPath);

        Assert.assertEquals(2, defects.size());
    }

    @Test
    public void checkForCorrect() {
        File testStartPath = new File("target/test-classes/checkPropertyExistsTest/correct");
        File file = new File(testStartPath, "checkForAllCorrect.properties");

        URL fileUrl = Util.getURLForFile(file);
        PropertyFileProperties propertyFileProperties = new PropertyFileProperties(fileUrl);

        List<Defect> defects = getDefects(new CheckPropertyExists(propertyFileProperties), testStartPath);

        Assert.assertEquals(0, defects.size());
    }
}
