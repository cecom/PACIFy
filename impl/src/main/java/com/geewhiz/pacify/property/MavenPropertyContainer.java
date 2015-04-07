package com.geewhiz.pacify.property;


import java.util.Collections;
import java.util.List;
import java.util.Properties;

import com.geewhiz.pacify.defect.Defect;

/**
 * User: sop
 * Date: 19.05.11
 * Time: 08:45
 */
public class MavenPropertyContainer implements PropertyContainer {

    Properties properties;
    String encoding;

    public MavenPropertyContainer(Properties properties, String encoding) {
        this.properties = properties;
        this.encoding = encoding;
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

    public String getEncoding() {
        return encoding;
    }
}
