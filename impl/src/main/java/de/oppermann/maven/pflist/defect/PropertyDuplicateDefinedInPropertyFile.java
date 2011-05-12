package de.oppermann.maven.pflist.defect;

import java.io.File;

/**
 * User: sop
 * Date: 03.05.11
 * Time: 17:13
 */
public class PropertyDuplicateDefinedInPropertyFile implements Defect {
    private String property;
    private File propertyFile;

    public PropertyDuplicateDefinedInPropertyFile(String property, File propertyFile) {
        this.property = property;
        this.propertyFile = propertyFile;
    }

    public String getDefectMessage() {
        return "Property [" + property + "] is duplicate defined in [" + propertyFile.getPath() + "]";
    }
}
