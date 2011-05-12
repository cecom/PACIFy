package de.oppermann.maven.pflist.defect;

import java.net.URL;

/**
 * User: sop
 * Date: 03.05.11
 * Time: 17:13
 */
public class PropertyDuplicateDefinedInPropertyFile implements Defect {
    private String property;
    private URL propertyFileURL;

    public PropertyDuplicateDefinedInPropertyFile(String property, URL propertyFileURL) {
        this.property = property;
        this.propertyFileURL = propertyFileURL;
    }

    public String getDefectMessage() {
        return "Property [" + property + "] is duplicate defined in [" + propertyFileURL.getPath() + "]";
    }
}
