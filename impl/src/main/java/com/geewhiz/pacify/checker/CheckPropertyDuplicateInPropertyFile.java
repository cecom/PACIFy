package com.geewhiz.pacify.checker;


import java.util.ArrayList;
import java.util.List;

import com.geewhiz.pacify.defect.Defect;
import com.geewhiz.pacify.property.PropertyContainer;

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
