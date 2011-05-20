package de.oppermann.maven.pflist.checker;

import de.oppermann.maven.pflist.defect.Defect;
import de.oppermann.maven.pflist.property.PropertyContainer;

import java.util.ArrayList;
import java.util.List;

/**
 * User: sop
 * Date: 03.05.11
 * Time: 13:05
 */
public class CheckPropertyDuplicateInPropertyFile implements Check {

    PropertyContainer propertyContainer;

    public CheckPropertyDuplicateInPropertyFile(PropertyContainer propertyContainer) {
        this.propertyContainer = propertyContainer;
    }

    public List<Defect> checkForErrors() {
        List<Defect> defects = new ArrayList<Defect>();

        defects.addAll(propertyContainer.checkForDuplicateEntry());

        return defects;
    }
}
