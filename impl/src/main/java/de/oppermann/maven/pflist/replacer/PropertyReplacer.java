package de.oppermann.maven.pflist.replacer;

import de.oppermann.maven.pflist.defect.Defect;
import de.oppermann.maven.pflist.defect.PropertyNotReplacedDefect;
import de.oppermann.maven.pflist.property.PFProperties;
import de.oppermann.maven.pflist.xml.PFFile;
import de.oppermann.maven.pflist.xml.PFList;
import de.oppermann.maven.pflist.xml.PFListProperty;
import org.apache.tools.ant.types.FilterSet;
import org.apache.tools.ant.types.FilterSetCollection;
import org.apache.tools.ant.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: sop
 * Date: 03.05.11
 * Time: 13:05
 */
public class PropertyReplacer {

    public static final String BEGIN_TOKEN = "%{";
    public static final String END_TOKEN = "}";

    private PFProperties pfProperties;
    private PFList pfList;

    public PropertyReplacer(PFProperties pfProperties, PFList pfList) {
        this.pfProperties = pfProperties;
        this.pfList = pfList;
    }

    public List<Defect> replace() {
        List<Defect> defects = new ArrayList<Defect>();
        for (PFFile pfFile : pfList.getPfFiles()) {
            List<PFListProperty> pfListProperties = pfList.getPfPropertiesForPfFile(pfFile);
            FilterSetCollection filterSetCollection = getFilterSetCollection(pfListProperties);

            File file = pfList.getAbsoluteFileFor(pfFile);
            File tmpFile = new File(file.getParentFile(), file.getName() + "_tmp");

            try {
                FileUtils.getFileUtils().copyFile(file, tmpFile, filterSetCollection, true, true);
                if (!file.delete())
                    throw new RuntimeException("Couldn't delete file [" + file.getPath() + "]... Aborting!");
                if (!tmpFile.renameTo(file))
                    throw new RuntimeException("Couldn't rename filtered file from [" + tmpFile.getPath() + "] to [" + file.getPath() + "]... Aborting!");
                defects.addAll(checkFileForNotReplacedStuff(file));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return defects;
    }

    private List<Defect> checkFileForNotReplacedStuff(File file) {
        List<Defect> defects = new ArrayList<Defect>();

        String fileContent = de.oppermann.maven.pflist.utils.FileUtils.getFileInOneString(file);

        Pattern pattern = PropertyReplacer.getPattern("([^}]*)", false);
        Matcher matcher = pattern.matcher(fileContent);

        while (matcher.find()) {
            String propertyId = matcher.group(1);
            Defect defect = new PropertyNotReplacedDefect(pfList, file, propertyId);
            defects.add(defect);
        }
        return defects;
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
            String propertyId = pfListProperty.getId();
            String propertyValue = pfProperties.getPropertyValue(propertyId);

            filterSet.addFilter(propertyId, propertyValue);

            Set<String> stackOverflowCheck = new TreeSet<String>();
            stackOverflowCheck.add(propertyId);

            //if a property contains another property which is not in the pflist.xml file, we have to add the other property too.
            for (String referencedPropertyId : getAllReferencedPropertyIds(stackOverflowCheck, propertyId, propertyValue)) {
                String referencedValue = pfProperties.getPropertyValue(referencedPropertyId);
                filterSet.addFilter(referencedPropertyId, referencedValue);
            }
        }
        return filterSet;
    }

    private Set<String> getAllReferencedPropertyIds(Set<String> parentPropertyIds, String parentPropertyId, String parentPropertyValue) {
        Set<String> result = new TreeSet<String>();

        Matcher matcher = getPattern("([^}]*)", false).matcher(parentPropertyValue);
        while (matcher.find()) {
            String propertyId = matcher.group(1);

            if (parentPropertyIds.contains(propertyId))
                throw new RuntimeException("You have a cycle reference in property [" + parentPropertyId + "] which is used in " +
                        "pflist file ["+pfList.getFile().getAbsolutePath()+"].");

            result.add(propertyId);

            String propertyValue = pfProperties.getPropertyValue(propertyId);
            Set<String> childStackOverflowCheck = new TreeSet<String>(parentPropertyIds);
            childStackOverflowCheck.add(propertyId);
            result.addAll(getAllReferencedPropertyIds(childStackOverflowCheck, propertyId, propertyValue));
        }
        return result;
    }

    public static Pattern getPattern(String match, boolean quoteIt) {
        String searchPattern = Pattern.quote(PropertyReplacer.BEGIN_TOKEN) + (quoteIt ? Pattern.quote(match) : match) + Pattern.quote(PropertyReplacer.END_TOKEN);

        return Pattern.compile(searchPattern);
    }
}
