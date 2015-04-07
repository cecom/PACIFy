package com.geewhiz.pacify;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.geewhiz.pacify.checker.CheckPropertyExists;
import com.geewhiz.pacify.defect.Defect;
import com.geewhiz.pacify.property.FilePropertyContainer;

import java.io.File;
import java.net.URL;
import java.util.List;

/**
 * User: sop
 * Date: 03.05.11
 * Time: 13:05
 */
public class TestCheckPropertyExistsInPropertyFile extends BaseCheck {

    @Test
    public void checkForNotCorrect() {
        File testStartPath = new File("target/test-classes/checkPropertyExistsTest/wrong");
        File file = new File(testStartPath, "checkForMissingProperty.properties");

        URL fileUrl = Util.getURLForFile(file);
        FilePropertyContainer filePropertyContainer = new FilePropertyContainer(fileUrl);

        List<Defect> defects = getDefects(new CheckPropertyExists(filePropertyContainer), testStartPath);

        Assert.assertEquals(2, defects.size());
    }

    @Test
    public void checkForCorrect() {
        File testStartPath = new File("target/test-classes/checkPropertyExistsTest/correct");
        File file = new File(testStartPath, "checkForAllCorrect.properties");

        URL fileUrl = Util.getURLForFile(file);
        FilePropertyContainer filePropertyContainer = new FilePropertyContainer(fileUrl);

        List<Defect> defects = getDefects(new CheckPropertyExists(filePropertyContainer), testStartPath);

        Assert.assertEquals(0, defects.size());
    }
}
