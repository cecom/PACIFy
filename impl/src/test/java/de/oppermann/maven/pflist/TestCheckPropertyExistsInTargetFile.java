package de.oppermann.maven.pflist;

import de.oppermann.maven.pflist.defect.Defect;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.util.List;

/**
 * User: sop
 * Date: 03.05.11
 * Time: 13:05
 */
public class TestCheckPropertyExistsInTargetFile extends BaseCheck {
    @Test
    public void checkForNotCorrect() {
        File testStartPath = new File("target/test-classes/checkPropertyExistsInTargetFileTest/wrong");

        List<Defect> defects = getDefects(new de.oppermann.maven.pflist.checker.CheckPropertyExistsInTargetFile(), testStartPath);

        Assert.assertEquals(2, defects.size());
    }

    @Test
    public void checkForCorrect() {
        File testStartPath = new File("target/test-classes/checkPropertyExistsInTargetFileTest/correct");

        List<Defect> defects = getDefects(new de.oppermann.maven.pflist.checker.CheckPropertyExistsInTargetFile(), testStartPath);

        Assert.assertEquals(0, defects.size());
    }
}
