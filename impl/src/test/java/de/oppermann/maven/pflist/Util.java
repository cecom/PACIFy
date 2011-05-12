package de.oppermann.maven.pflist;

import de.oppermann.maven.pflist.xml.utils.DirFilter;
import org.apache.tools.ant.util.FileUtils;
import org.junit.Assert;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * User: sop
 * Date: 06.05.11
 * Time: 09:28
 */
public class Util {

    public static void checkIfResultIsAsExpected(File startPath) {
        File dirWithFilesWhichTheyShouldLookLike = new File(startPath.getPath() + "_ResultFiles");
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

    private static List<File> getFiles(File folder) {
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
