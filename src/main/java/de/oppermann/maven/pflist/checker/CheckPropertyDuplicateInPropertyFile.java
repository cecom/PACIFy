package de.oppermann.maven.pflist.checker;

import de.oppermann.maven.pflist.defect.Defect;
import de.oppermann.maven.pflist.defect.PropertyDuplicateDefinedInPropertyFile;
import de.oppermann.maven.pflist.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User: sop
 * Date: 03.05.11
 * Time: 13:05
 */
public class CheckPropertyDuplicateInPropertyFile implements Check {

    private File propertyFile;

    public CheckPropertyDuplicateInPropertyFile(File propertyFile) {
        this.propertyFile = propertyFile;
    }

    public List<Defect> checkForErrors() {
        List<Defect> defects = new ArrayList<Defect>();

        Set<String> propertyIds = new HashSet<String>();

        for (String line : FileUtils.getFileAsLines(propertyFile)) {
            String[] split = line.split("=");
            String propertyId = split[0];
            boolean couldBeAdded = propertyIds.add(propertyId);
            if (!couldBeAdded) {
                Defect defect = new PropertyDuplicateDefinedInPropertyFile(propertyId, propertyFile);
                defects.add(defect);
            }
        }

        return defects;
    }
}
