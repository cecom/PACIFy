package de.oppermann.maven.pflist.defect;

import de.oppermann.maven.pflist.property.PropertyContainer;

/**
 * User: sop
 * Date: 03.05.11
 * Time: 17:13
 */
public class PropertyDuplicateDefinedInPropertyFile implements Defect {
    private String property;
    private PropertyContainer propertyContainer;

    public PropertyDuplicateDefinedInPropertyFile(String property, PropertyContainer propertyContainer) {
        this.property = property;
        this.propertyContainer = propertyContainer;
    }

    public String getDefectMessage() {
        return "Property [" + property + "] is duplicate defined in property file [" + propertyContainer.getPropertyLoadedFrom() + "]";
    }
}
