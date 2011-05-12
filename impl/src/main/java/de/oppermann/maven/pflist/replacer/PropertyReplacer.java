package de.oppermann.maven.pflist.replacer;

import de.oppermann.maven.pflist.xml.PFFile;
import de.oppermann.maven.pflist.xml.PFList;
import de.oppermann.maven.pflist.xml.PFProperty;
import org.apache.tools.ant.types.FilterSet;
import org.apache.tools.ant.types.FilterSetCollection;
import org.apache.tools.ant.util.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

/**
 * User: sop
 * Date: 03.05.11
 * Time: 13:05
 */
public class PropertyReplacer {

    public static final String BEGIN_TOKEN = "%{";
    public static final String END_TOKEN = "}";

    private File propertyFile;
    private Properties properties;
    private FileUtils fileUtils;

    public PropertyReplacer(File propertyFile) {
        this.propertyFile = propertyFile;
        fileUtils = FileUtils.getFileUtils();
    }

    public void replace(PFList pfList) {
        for (PFFile pfFile : pfList.getPfFiles()) {
            List<PFProperty> pfProperties = pfList.getPfPropertiesForPfFile(pfFile);
            FilterSetCollection filterSetCollection = getFilterSetCollection(pfProperties);

            File file = pfList.getAbsoluteFileFor(pfFile);
            File tmpFile = new File(file.getParentFile(), file.getName() + "_tmp");

            try {
                fileUtils.copyFile(file, tmpFile, filterSetCollection, true, true);
                if (!file.delete())
                    throw new RuntimeException("Couldn't delete file [" + file.getPath() + "]... Aborting!");
                if (!tmpFile.renameTo(file))
                    throw new RuntimeException("Couldn't rename filtered file from [" + tmpFile.getPath() + "] to [" + file.getPath() + "]... Aborting!");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void fillFilterSet(FilterSet filterSet, List<PFProperty> pfProperties) {
        filterSet.setBeginToken(BEGIN_TOKEN);
        filterSet.setEndToken(END_TOKEN);

        for (PFProperty pfProperty : pfProperties) {
            String key = pfProperty.getId();
            String value = getProperties().getProperty(key);
            filterSet.addFilter(key, value);
        }
    }

    private FilterSetCollection getFilterSetCollection(List<PFProperty> pfProperties) {
        FilterSet filterSet = getFilterSet(pfProperties);

        FilterSetCollection executionFilters = new FilterSetCollection();
        executionFilters.addFilterSet(filterSet);
        return executionFilters;
    }

    private FilterSet getFilterSet(List<PFProperty> pfProperties) {
        FilterSet filterSet = new FilterSet();

        filterSet.setBeginToken(BEGIN_TOKEN);
        filterSet.setEndToken(END_TOKEN);

        for (PFProperty pfProperty : pfProperties) {
            String key = pfProperty.getId();
            String value = getProperties().getProperty(key);
            filterSet.addFilter(key, value);
        }
        return filterSet;
    }

    private Properties getProperties() {
        if (properties == null) {
            properties = new Properties();
            try {
                properties.load(new FileInputStream(propertyFile));
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return properties;
    }
}
