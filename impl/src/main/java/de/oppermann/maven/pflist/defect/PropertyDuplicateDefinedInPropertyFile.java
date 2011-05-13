package de.oppermann.maven.pflist.defect;

import de.oppermann.maven.pflist.property.PropertyFile;

/**
 * User: sop
 * Date: 03.05.11
 * Time: 17:13
 */
public class PropertyDuplicateDefinedInPropertyFile implements Defect {
    private String property;
    private PropertyFile propertyFile;

    public PropertyDuplicateDefinedInPropertyFile(String property, PropertyFile propertyFile) {
        this.property = property;
        this.propertyFile = propertyFile;
    }

    public String getDefectMessage() {
        return "Property [" + property + "] is duplicate defined in [" + propertyFile.getPropertyFileURL().getPath() + "]";
    }
}
