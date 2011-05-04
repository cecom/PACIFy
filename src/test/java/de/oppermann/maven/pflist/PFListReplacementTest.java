package de.oppermann.maven.pflist;

import de.oppermann.maven.pflist.commandline.CommandLineParameter;
import de.oppermann.maven.pflist.logger.LogLevel;
import de.oppermann.maven.pflist.xml.utils.DirFilter;
import org.apache.tools.ant.util.FileUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;

/**
 * User: sop
 * Date: 03.05.11
 * Time: 13:05
 */
public class PFListReplacementTest {

    @Test
    public void testAll() {
        File startPath = new File("target/test-classes/testAll");
        File myTestProperty = new File(startPath, "myTest.properties");

        Assert.assertTrue("StartPath [" + startPath.getPath() + "] doesn't exist!", startPath.exists());

        EnumMap<CommandLineParameter, Object> commandlineProperties = new EnumMap<CommandLineParameter, Object>(CommandLineParameter.class);
        commandlineProperties.put(CommandLineParameter.StartPath, startPath);
        commandlineProperties.put(CommandLineParameter.PropertyFile, myTestProperty);
        commandlineProperties.put(CommandLineParameter.LogLevel, LogLevel.DEBUG);

        PFListPropertyReplacer pfListPropertyReplacer = new PFListPropertyReplacer(commandlineProperties);
        pfListPropertyReplacer.replace();

        checkIfResultIsAsExpected(startPath);
    }

    private void checkIfResultIsAsExpected(File startPath) {
        File dirWithFilesWhichTheyShouldLookLike = new File("target/test-classes/testAll_ResultFiles");
        List<File> filesToCompare = getFiles(dirWithFilesWhichTheyShouldLookLike);
        for (File resultFile : filesToCompare) {
            String completeRelativePath = dirWithFilesWhichTheyShouldLookLike.getPath();
            int index = resultFile.getPath().indexOf(completeRelativePath) + completeRelativePath.length();
            String relativePath = resultFile.getPath().substring(index);

            File filteredFile = new File(startPath, relativePath);
            try {
                Assert.assertTrue("Filtered file does not have the expected result. The content of the File should look like ["
                        + resultFile.getPath() + "] but is [" + filteredFile.getPath() + "]."
                        , FileUtils.getFileUtils().contentEquals(resultFile, filteredFile));

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private List<File> getFiles(File folder) {
         List<File> files = new ArrayList<File>();

        Collections.addAll(files, folder.listFiles(new FileFilter() {
            public boolean accept(File pathName) {
                return pathName.isFile();
            }
        }));

        for (File subFolder : folder.listFiles(new DirFilter()))
            files.addAll(getFiles(subFolder));

        return files;
    }
}
