package de.oppermann.maven.pflist.defect;

import de.oppermann.maven.pflist.xml.PFList;

import java.io.File;

/**
 * User: sop
 * Date: 17.05.11
 * Time: 12:16
 */
public class PropertyNotReplacedDefect implements Defect {

    File file;
    String propertyId;
    private PFList pfList;


    public PropertyNotReplacedDefect(PFList pfList, File file, String propertyId) {
        this.pfList = pfList;
        this.file = file;
        this.propertyId = propertyId;
    }

    public String getDefectMessage() {
        return "In file [" + file.getAbsolutePath() + "] is a property " +
                "[" + propertyId + "] which is not replaced. Did you forget it in [" + pfList.getFile().getAbsolutePath() + "] or is it unnecessary?";

    }
}