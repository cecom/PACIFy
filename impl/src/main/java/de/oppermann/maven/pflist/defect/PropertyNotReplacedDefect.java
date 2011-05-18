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


    public PropertyNotReplacedDefect(File file, String propertyId) {
        this.file = file;
        this.propertyId = propertyId;
    }

    public String getDefectMessage() {
        return "In file [" + file.getAbsolutePath() + "] is a property " +
                "[" + propertyId + "] which is not replaced. Did you forget it or is it unnecessary?";

    }
}