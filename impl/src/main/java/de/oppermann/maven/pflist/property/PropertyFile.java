package de.oppermann.maven.pflist.property;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * User: sop
 * Date: 13.05.11
 * Time: 12:38
 */
public class PropertyFile {

    public static final String IMPORT_STRING = "#!import";

    private URL propertyFileURL;

    private boolean initialized = false;
    private Properties localProperties;
    private Properties properties;
    private List<PropertyFile> parentPropertyFiles = new ArrayList<PropertyFile>();

    public PropertyFile(URL propertyFileURL) {
        this.propertyFileURL = propertyFileURL;
    }

    public boolean hasProperty(String key) {
        return getPropertyValue(key) != null;
    }

    public String getPropertyValue(String key) {
        return getProperties().getProperty(key);
    }

    /**
     * @return the localProperties for this instance
     */
    public Properties getLocalProperties() {
        if (!initialized)
            initialize();
        return localProperties;
    }

    /**
     * @return the localProperties for this instance and its parents.
     */
    public Properties getProperties() {
         if (!initialized)
            initialize();
        return properties;
    }

    public List<PropertyFile> getParentPropertyFiles() {
        return parentPropertyFiles;
    }

    public URL getPropertyFileURL() {
        return propertyFileURL;
    }

    private void initialize() {
        initialized = true;
        PropertyFileLoaderUtils.loadPropertyFile(this);

        properties = new Properties();
        for (PropertyFile parentPropertyFile : getParentPropertyFiles()) {
            Properties parentProperties = parentPropertyFile.getProperties();
            properties.putAll(parentProperties);
        }
        properties.putAll(localProperties);
    }

    protected void setLocalProperties(Properties localProperties) {
        this.localProperties = localProperties;
    }

    protected void addParentPropertyFile(PropertyFile parent) {
        parent.initialize();
        parentPropertyFiles.add(parent);
    }
}
