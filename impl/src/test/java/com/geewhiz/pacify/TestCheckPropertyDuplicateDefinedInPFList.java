package com.geewhiz.pacify;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.geewhiz.pacify.defect.Defect;

import java.io.File;
import java.util.List;

/**
 * User: sop
 * Date: 03.05.11
 * Time: 13:05
 */
public class TestCheckPropertyDuplicateDefinedInPFList extends BaseCheck {

    @Test
    public void checkForNotCorrect() {
        File testStartPath = new File("target/test-classes/checkPropertyDuplicateDefinedInPfListCheck/wrong");

        List<Defect> defects = getDefects(new com.geewhiz.pacify.checker.CheckPropertyDuplicateDefinedInPFList(), testStartPath);

        Assert.assertEquals(1, defects.size());
    }

    @Test
    public void checkForCorrect() {
        File testStartPath = new File("target/test-classes/checkPropertyDuplicateDefinedInPfListCheck/correct");

        List<Defect> defects = getDefects(new com.geewhiz.pacify.checker.CheckPropertyDuplicateDefinedInPFList(), testStartPath);

        Assert.assertEquals(0, defects.size());
    }

}