package de.oppermann.maven.pflist.replacer;

import de.oppermann.maven.pflist.defect.Defect;
import de.oppermann.maven.pflist.logger.Log;
import de.oppermann.maven.pflist.logger.LogLevel;
import de.oppermann.maven.pflist.model.PFFileEntity;
import de.oppermann.maven.pflist.model.PFListEntity;
import de.oppermann.maven.pflist.model.PFPropertyEntity;
import de.oppermann.maven.pflist.property.PropertyContainer;
import de.oppermann.maven.pflist.utils.Utils;
import org.apache.tools.ant.types.FilterSet;
import org.apache.tools.ant.types.FilterSetCollection;
import org.apache.tools.ant.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;

/**
 * User: sop
 * Date: 03.05.11
 * Time: 13:05
 */
public class PropertyPFReplacer {

    private PropertyContainer propertyContainer;
    private PFListEntity pfListEntity;

    public PropertyPFReplacer(PropertyContainer propertyContainer, PFListEntity pfListEntity) {
        this.propertyContainer = propertyContainer;
        this.pfListEntity = pfListEntity;
    }

    public List<Defect> replace() {
        List<Defect> defects = new ArrayList<Defect>();
        for (PFFileEntity pfFileEntity : pfListEntity.getPfFileEntities()) {

            FilterSetCollection filterSetCollection = getFilterSetCollection(pfFileEntity);

            File file = pfListEntity.getAbsoluteFileFor(pfFileEntity);
            File tmpFile = new File(file.getParentFile(), file.getName() + "_tmp");

            try {
                String encoding = de.oppermann.maven.pflist.utils.FileUtils.getEncoding(file);
                Log.log(LogLevel.INFO, "Using  encoding [" + encoding + "] for  File  [" + file.getAbsolutePath() + "]");
                FileUtils.getFileUtils().copyFile(file, tmpFile, filterSetCollection, true, true, encoding);
                if (!file.delete())
                    throw new RuntimeException("Couldn't delete file [" + file.getPath() + "]... Aborting!");
                if (!tmpFile.renameTo(file))
                    throw new RuntimeException("Couldn't rename filtered file from [" + tmpFile.getPath() + "] to [" + file.getPath() + "]... Aborting!");
                defects.addAll(Utils.checkFileForNotReplacedStuff(file));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return defects;
    }

    private FilterSetCollection getFilterSetCollection(PFFileEntity pfFileEntity) {
        FilterSet filterSet = getFilterSet(pfFileEntity);

        FilterSetCollection executionFilters = new FilterSetCollection();
        executionFilters.addFilterSet(filterSet);

        return executionFilters;
    }

    private FilterSet getFilterSet(PFFileEntity pfFileEntity) {
        FilterSet filterSet = new FilterSet();

        filterSet.setBeginToken(PropertyFileReplacer.BEGIN_TOKEN);
        filterSet.setEndToken(PropertyFileReplacer.END_TOKEN);

        List<PFPropertyEntity> pfPropertyEntities = pfListEntity.getPfPropertyEntitiesForPFFileEntity(pfFileEntity);

        for (PFPropertyEntity pfPropertyEntity : pfPropertyEntities) {
            String propertyId = pfPropertyEntity.getId();
            String propertyValue = propertyContainer.getPropertyValue(propertyId);

            if (pfPropertyEntity.convertBackslashToSlash()) {
                String convertedString = propertyValue;
                convertedString = propertyValue.replace('\\', '/');
                Log.log(LogLevel.INFO, " Converting backslashes [" + propertyValue + "] to slashes [" + convertedString + "]");
                propertyValue = convertedString;
            }

            filterSet.addFilter(propertyId, propertyValue);

            //needed for checking that we don't have a property which references another property, which references this property (cycle)
            Set<String> propertyResolvePath = new TreeSet<String>();
            propertyResolvePath.add(propertyId);

            //if a property contains another property which is not in the pflist.xml file, we have to add the other property too.
            for (String referencedPropertyId : getAllReferencedPropertyIds(propertyResolvePath, propertyId, propertyValue)) {
                String referencedValue = propertyContainer.getPropertyValue(referencedPropertyId);
                filterSet.addFilter(referencedPropertyId, referencedValue);
            }
        }
        return filterSet;
    }

    private Set<String> getAllReferencedPropertyIds(Set<String> parentPropertyResolvePath, String parentPropertyId, String parentPropertyValue) {
        if (parentPropertyValue == null)
            return Collections.emptySet();

        Set<String> result = new TreeSet<String>();

        Matcher matcher = PropertyFileReplacer.getPattern("([^}]*)", false).matcher(parentPropertyValue);
        while (matcher.find()) {
            String propertyId = matcher.group(1);

            if (parentPropertyResolvePath.contains(propertyId))
                throw new RuntimeException("You have a cycle reference in property [" + parentPropertyId + "] which is used in " +
                        "pflist file [" + pfListEntity.getFile().getAbsolutePath() + "]. Property values loaded from [" + propertyContainer.getPropertyLoadedFrom() + "]");

            result.add(propertyId);

            Set<String> propertyResolvePath = new TreeSet<String>(parentPropertyResolvePath);
            propertyResolvePath.add(propertyId);

            String propertyValue = propertyContainer.getPropertyValue(propertyId);
            result.addAll(getAllReferencedPropertyIds(propertyResolvePath, propertyId, propertyValue));
        }
        return result;
    }
}
