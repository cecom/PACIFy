package de.oppermann.maven.pflist.property;

import de.oppermann.maven.pflist.defect.Defect;

import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 * User: sop
 * Date: 19.05.11
 * Time: 08:45
 */
public class MavenPropertyContainer implements PropertyContainer {

    Properties properties;

    public MavenPropertyContainer(Properties properties) {
        this.properties = properties;
    }

    public boolean containsKey(String key) {
        return getProperties().containsKey(key);
    }

    public String getPropertyValue(String key) {
        if (containsKey(key))
            return getProperties().getProperty(key);
        throw new IllegalArgumentException("Property [" + key + "] not defined within maven... Aborting!");
    }

    public Properties getProperties() {
        return properties;
    }

    public List<Defect> checkForDuplicateEntry() {
        return Collections.emptyList();
    }

    public String getPropertyLoadedFrom() {
        return "maven";
    }
}
