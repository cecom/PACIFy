package de.oppermann.maven.pflist.replacer;

import de.oppermann.maven.pflist.property.PFProperties;
import de.oppermann.maven.pflist.xml.PFFile;
import de.oppermann.maven.pflist.xml.PFList;
import de.oppermann.maven.pflist.xml.PFListProperty;
import org.apache.tools.ant.types.FilterSet;
import org.apache.tools.ant.types.FilterSetCollection;
import org.apache.tools.ant.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * User: sop
 * Date: 03.05.11
 * Time: 13:05
 */
public class PropertyReplacer {

    public static final String BEGIN_TOKEN = "%{";
    public static final String END_TOKEN = "}";

    private PFProperties pfProperties;
    private FileUtils fileUtils;

    public PropertyReplacer(PFProperties pfProperties) {
        this.pfProperties = pfProperties;
        fileUtils = FileUtils.getFileUtils();
    }

    public void replace(PFList pfList) {
        for (PFFile pfFile : pfList.getPfFiles()) {
            List<PFListProperty> pfListProperties = pfList.getPfPropertiesForPfFile(pfFile);
            FilterSetCollection filterSetCollection = getFilterSetCollection(pfListProperties);

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

    private FilterSetCollection getFilterSetCollection(List<PFListProperty> pfListProperties) {
        FilterSet filterSet = getFilterSet(pfListProperties);

        FilterSetCollection executionFilters = new FilterSetCollection();
        executionFilters.addFilterSet(filterSet);
        return executionFilters;
    }

    private FilterSet getFilterSet(List<PFListProperty> pfListProperties) {
        FilterSet filterSet = new FilterSet();

        filterSet.setBeginToken(BEGIN_TOKEN);
        filterSet.setEndToken(END_TOKEN);

        for (PFListProperty pfListProperty : pfListProperties) {
            String key = pfListProperty.getId();
            String value = this.pfProperties.getPropertyValue(key);
            filterSet.addFilter(key, value);
        }
        return filterSet;
    }
}
