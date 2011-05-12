package de.oppermann.maven.pflist;

import de.oppermann.maven.pflist.checker.CheckPropertyExists;
import de.oppermann.maven.pflist.defect.Defect;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
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
        File propertyFile = new File(testStartPath, "checkForMissingProperty.properties");

        List<Defect> defects = getDefects(new CheckPropertyExists(propertyFile), testStartPath, propertyFile);

        Assert.assertEquals(2, defects.size());
    }

    @Test
    public void checkForCorrect() {
        File testStartPath = new File("target/test-classes/checkPropertyExistsTest/correct");
        File propertyFile = new File(testStartPath, "checkForAllCorrect.properties");

        List<Defect> defects = getDefects(new CheckPropertyExists(propertyFile), testStartPath, propertyFile);

        Assert.assertEquals(0, defects.size());
    }
}
