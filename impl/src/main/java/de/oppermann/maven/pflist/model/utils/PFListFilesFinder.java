package de.oppermann.maven.pflist.model.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * User: sop
 * Date: 03.05.11
 * Time: 13:05
 */

public class PFListFilesFinder {
    private static PFListFilenameFilter pfListFilenameFilter = new PFListFilenameFilter();
    private static DirFilter dirFilter = new DirFilter();

    private File folderToCheck;

    public PFListFilesFinder(File folderToCheck) {
        this.folderToCheck = folderToCheck;
    }

    public List<File> getPFListFiles() {
        List<File> pfListFiles = new ArrayList<File>();
        addPFListFiles(pfListFiles, folderToCheck);
        return pfListFiles;
    }

    private void addPFListFiles(List<File> pfListFiles, File folderToCheck) {
        if (folderToCheck == null)
            throw new IllegalArgumentException("Folder is null.... Aborting!");
        if (!folderToCheck.exists())
            throw new IllegalArgumentException("Folder [" + folderToCheck.getAbsolutePath() + "] does not exist... Aborting!");

        pfListFiles.addAll(Arrays.asList(folderToCheck.listFiles(pfListFilenameFilter)));

        File[] subFolders = folderToCheck.listFiles(dirFilter);
        for (File subFolder : subFolders)
            addPFListFiles(pfListFiles, subFolder);
    }
}
