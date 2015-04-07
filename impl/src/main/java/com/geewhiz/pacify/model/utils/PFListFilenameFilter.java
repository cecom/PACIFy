package com.geewhiz.pacify.model.utils;

import java.io.File;
import java.io.FilenameFilter;

/**
 * User: sop
 * Date: 03.05.11
 * Time: 13:05
 */
public class PFListFilenameFilter implements FilenameFilter {

    /**
     * Only accept PFList Files
     */
    public boolean accept(File dir, String name) {
        return name.endsWith("-PFList.xml");
    }
}
