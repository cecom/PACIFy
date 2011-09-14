package de.oppermann.maven.pflist.property;

import de.oppermann.maven.pflist.defect.Defect;
import de.oppermann.maven.pflist.defect.PropertyDuplicateDefinedInPropertyFile;
import de.oppermann.maven.pflist.utils.FileUtils;
import de.oppermann.maven.pflist.utils.Utils;

import java.io.*;
import java.net.URL;
import java.util.*;

/**
 * User: sop
 * Date: 13.05.11
 * Time: 12:38
 */
public class FilePropertyContainer implements PropertyContainer {

    public static final String IMPORT_STRING = "#!import";

    private URL propertyFileURL;

    private boolean initialized = false;
    private Properties localProperties;
    private Properties properties;
    private List<FilePropertyContainer> parentFileProperties = new ArrayList<FilePropertyContainer>();

    public FilePropertyContainer(URL propertyFileURL) {
        this.propertyFileURL = propertyFileURL;
    }

    public boolean containsKey(String key) {
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

    public String getPropertyLoadedFrom() {
        return getPropertyFileURL().toString();
    }

    public List<Defect> checkForDuplicateEntry() {
        List<Defect> defects = new ArrayList<Defect>();

        Set<String> propertyIds = new HashSet<String>();

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

        for (FilePropertyContainer parentFilePropertyContainer : getParentPropertyFileProperties())
            defects.addAll(parentFilePropertyContainer.checkForDuplicateEntry());
        return defects;
    }

    public URL getPropertyFileURL() {
        return propertyFileURL;
    }


    public List<FilePropertyContainer> getParentPropertyFileProperties() {
        return parentFileProperties;
    }


    private void initialize() {
        initialized = true;
        loadPropertyFile(this);

        properties = new Properties();
        for (FilePropertyContainer parentFilePropertyContainer : getParentPropertyFileProperties()) {
            Properties parentProperties = parentFilePropertyContainer.getProperties();
            properties.putAll(parentProperties);
        }
        properties.putAll(localProperties);
    }

    protected void setLocalProperties(Properties localProperties) {
        this.localProperties = localProperties;
    }

    protected void addParentPropertyFile(FilePropertyContainer parent) {
        parent.initialize();
        parentFileProperties.add(parent);
    }

    public void loadPropertyFile(FilePropertyContainer filePropertyContainer) {
        InputStreamReader isr = null;
        try {
            URL propertyFileURL = filePropertyContainer.getPropertyFileURL();

            String encoding = Utils.getEncoding(propertyFileURL);

            isr = getInputStreamReaderFor(propertyFileURL, encoding);
            BufferedReader br = new BufferedReader(isr);

            ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(byteArray, encoding));

            for (String line; (line = br.readLine()) != null; ) {
                if (line.startsWith(FilePropertyContainer.IMPORT_STRING)) {
                    String[] includes = line.substring(FilePropertyContainer.IMPORT_STRING.length()).trim().split(" ");
                    for (String include : includes) {
                        URL parentPropertyFileURL = new URL(propertyFileURL, include);
                        FilePropertyContainer parentFilePropertyContainer = new FilePropertyContainer(parentPropertyFileURL);
                        filePropertyContainer.addParentPropertyFile(parentFilePropertyContainer);
                    }
                    continue;
                }
                bw.write(line);
                bw.newLine();
            }
            bw.flush();

            Properties properties = new Properties();
            properties.load(new StringReader(byteArray.toString(encoding)));
            filePropertyContainer.setLocalProperties(properties);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (isr != null) {
                try {
                    isr.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    private InputStreamReader getInputStreamReaderFor(URL propertyFilePathURL, String encoding) {
        InputStreamReader result;
        try {
            result = new InputStreamReader(propertyFilePathURL.openStream(), encoding);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (result == null)
            throw new RuntimeException("Couldn't find resource [" + propertyFilePathURL + "] in classpath.");
        return result;
    }
}
