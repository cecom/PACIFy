package de.oppermann.maven.pflist.property;

import de.oppermann.maven.pflist.defect.Defect;
import de.oppermann.maven.pflist.defect.PropertyDuplicateDefinedInPropertyFile;
import de.oppermann.maven.pflist.utils.FileUtils;

import java.io.*;
import java.net.URL;
import java.util.*;

/**
 * User: sop
 * Date: 13.05.11
 * Time: 12:38
 */
public class PropertyFileProperties implements PFProperties {

    public static final String IMPORT_STRING = "#!import";

    private URL propertyFileURL;

    private boolean initialized = false;
    private Properties localProperties;
    private Properties properties;
    private List<PropertyFileProperties> parentPropertyFilePropertieses = new ArrayList<PropertyFileProperties>();

    public PropertyFileProperties(URL propertyFileURL) {
        this.propertyFileURL = propertyFileURL;
    }

    public boolean contains(String key) {
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

    public List<Defect> checkForDuplicateEntry() {
        List<Defect> defects = new ArrayList<Defect>();

        Set<String> propertyIds = new HashSet<String>();

        InputStream is = null;
        try {
            is = getPropertyFileURL().openStream();
        } catch (IOException e) {
            throw new RuntimeException("Couldnt open stream for file [" + getPropertyFileURL().getPath() + "].");
        }

        for (String line : FileUtils.getFileAsLines(getPropertyFileURL())) {
            if (line.startsWith("#"))
                continue;
            if (line.trim().isEmpty())
                continue;

            String[] split = line.split("=");
            String propertyId = split[0];
            boolean couldBeAdded = propertyIds.add(propertyId);
            if (!couldBeAdded) {
                Defect defect = new PropertyDuplicateDefinedInPropertyFile(propertyId, this);
                defects.add(defect);
            }
        }

        for (PropertyFileProperties parentPropertyFileProperties : getParentPropertyFileProperties())
            defects.addAll(parentPropertyFileProperties.checkForDuplicateEntry());
        return defects;
    }

    public URL getPropertyFileURL() {
        return propertyFileURL;
    }


    public List<PropertyFileProperties> getParentPropertyFileProperties() {
        return parentPropertyFilePropertieses;
    }


    private void initialize() {
        initialized = true;
        loadPropertyFile(this);

        properties = new Properties();
        for (PropertyFileProperties parentPropertyFileProperties : getParentPropertyFileProperties()) {
            Properties parentProperties = parentPropertyFileProperties.getProperties();
            properties.putAll(parentProperties);
        }
        properties.putAll(localProperties);
    }

    protected void setLocalProperties(Properties localProperties) {
        this.localProperties = localProperties;
    }

    protected void addParentPropertyFile(PropertyFileProperties parent) {
        parent.initialize();
        parentPropertyFilePropertieses.add(parent);
    }

    public void loadPropertyFile(PropertyFileProperties propertyFileProperties) {
        try {
            URL propertyFileURL = propertyFileProperties.getPropertyFileURL();

            InputStream is = getInputStreamFor(propertyFileURL);
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);

            ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(byteArray));

            for (String line; (line = br.readLine()) != null;) {
                if (line.startsWith(PropertyFileProperties.IMPORT_STRING)) {
                    String[] includes = line.substring(PropertyFileProperties.IMPORT_STRING.length()).trim().split(" ");
                    for (String include : includes) {
                        URL parentPropertyFileURL = new URL(propertyFileURL, include);
                        PropertyFileProperties parentPropertyFileProperties = new PropertyFileProperties(parentPropertyFileURL);
                        propertyFileProperties.addParentPropertyFile(parentPropertyFileProperties);
                    }
                    continue;
                }
                bw.write(line);
                bw.newLine();
            }
            bw.flush();

            Properties properties = new Properties();
            properties.load(new ByteArrayInputStream(byteArray.toByteArray()));
            propertyFileProperties.setLocalProperties(properties);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private InputStream getInputStreamFor(URL propertyFilePathURL) {
        InputStream result;
        try {
            result = propertyFilePathURL.openStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (result == null)
            throw new RuntimeException("Couldn't find resource [" + propertyFilePathURL + "] in classpath.");
        return result;
    }

}
