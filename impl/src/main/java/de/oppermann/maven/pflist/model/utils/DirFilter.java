package de.oppermann.maven.pflist.model.utils;

import java.io.File;
import java.io.FileFilter;

/**
 * User: sop
 * Date: 03.05.11
 * Time: 13:05
 */
public class DirFilter implements FileFilter {

    public boolean accept(File pathName) {
        return pathName.isDirectory();
    }

}
