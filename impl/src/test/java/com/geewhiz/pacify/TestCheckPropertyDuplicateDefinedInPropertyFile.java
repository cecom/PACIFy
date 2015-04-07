package com.geewhiz.pacify;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.geewhiz.pacify.checker.CheckPropertyDuplicateInPropertyFile;
import com.geewhiz.pacify.defect.Defect;
import com.geewhiz.pacify.property.FilePropertyContainer;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * User: sop
 * Date: 03.05.11
 * Time: 13:05
 */
public class TestCheckPropertyDuplicateDefinedInPropertyFile {
    @Test
    public void checkForNotCorrect() {
        File testStartPath = new File("target/test-classes/checkPropertyDuplicateDefinedInPropertyFile/wrong");
        File file = new File(testStartPath, "myProperties.properties");

        URL fileUrl = Util.getURLForFile(file);
        FilePropertyContainer filePropertyContainer = new FilePropertyContainer(fileUrl);

        CheckPropertyDuplicateInPropertyFile checker = new CheckPropertyDuplicateInPropertyFile(filePropertyContainer);

        List<Defect> defects = new ArrayList<Defect>();
        defects.addAll(checker.checkForErrors());

        Assert.assertEquals(2, defects.size());
    }

    @Test
    public void checkForCorrect() {
        File testStartPath = new File("target/test-classes/checkPropertyDuplicateDefinedInPropertyFile/correct");
        File file = new File(testStartPath, "myProperties.properties");

        URL fileUrl = Util.getURLForFile(file);
        FilePropertyContainer filePropertyContainer = new FilePropertyContainer(fileUrl);

        CheckPropertyDuplicateInPropertyFile checker = new CheckPropertyDuplicateInPropertyFile(filePropertyContainer);

        List<Defect> defects = new ArrayList<Defect>();
        defects.addAll(checker.checkForErrors());

        Assert.assertEquals(0, defects.size());
    }
}
