package de.oppermann.maven.pflist;

import de.oppermann.maven.pflist.checker.CheckPropertyExistsInTargetFile;
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
public class CheckPropertyExistsInTargetFileTest extends CheckTest {
    @Test
    public void checkForNotCorrect() {
        File testStartPath = new File("target/test-classes/checkPropertyExistsInTargetFileTest/wrong");
        File propertyFile = new File(testStartPath, "myProperties.properties");

        List<Defect> defects = getDefects(new CheckPropertyExistsInTargetFile(), testStartPath, propertyFile);

        Assert.assertEquals(2, defects.size());
    }

    @Test
    public void checkForCorrect() {
        File testStartPath = new File("target/test-classes/checkPropertyExistsInTargetFileTest/correct");
        File propertyFile = new File(testStartPath, "myProperties.properties");

        List<Defect> defects = getDefects(new CheckPropertyExistsInTargetFile(), testStartPath, propertyFile);

        Assert.assertEquals(0, defects.size());
    }
}
