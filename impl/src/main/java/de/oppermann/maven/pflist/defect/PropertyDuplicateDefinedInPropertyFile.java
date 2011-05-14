package de.oppermann.maven.pflist.defect;

import de.oppermann.maven.pflist.property.PropertyFileProperties;

/**
 * User: sop
 * Date: 03.05.11
 * Time: 17:13
 */
public class PropertyDuplicateDefinedInPropertyFile implements Defect {
    private String property;
    private PropertyFileProperties propertyFileProperties;

    public PropertyDuplicateDefinedInPropertyFile(String property, PropertyFileProperties propertyFileProperties) {
        this.property = property;
        this.propertyFileProperties = propertyFileProperties;
    }

    public String getDefectMessage() {
        return "Property [" + property + "] is duplicate defined in [" + propertyFileProperties.getPropertyFileURL().getPath() + "]";
    }
}
