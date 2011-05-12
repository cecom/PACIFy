package de.oppermann.maven.pflist;

import de.oppermann.maven.pflist.checker.CheckPropertyDuplicateDefinedInPFList;
import de.oppermann.maven.pflist.defect.Defect;
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
public class CheckPropertyDuplicateDefinedInPFListCheck extends CheckTest {
    @Test
    public void checkForNotCorrect() {
        File testStartPath = new File("target/test-classes/checkPropertyDuplicateDefinedInPfListCheck/wrong");
        File propertyFile = new File(testStartPath, "myProperties.properties");

        URL fileUrl = Util.getURLForFile(propertyFile);

        List<Defect> defects = getDefects(new CheckPropertyDuplicateDefinedInPFList(), testStartPath, propertyFile);

        Assert.assertEquals(1, defects.size());
    }

    @Test
    public void checkForCorrect() {
        File testStartPath = new File("target/test-classes/checkPropertyDuplicateDefinedInPfListCheck/correct");
        File propertyFile = new File(testStartPath, "myProperties.properties");

        List<Defect> defects = getDefects(new CheckPropertyDuplicateDefinedInPFList(), testStartPath, propertyFile);

        Assert.assertEquals(0, defects.size());
    }

}