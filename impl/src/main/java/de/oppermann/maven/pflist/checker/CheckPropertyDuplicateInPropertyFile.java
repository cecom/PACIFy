package de.oppermann.maven.pflist.checker;

import de.oppermann.maven.pflist.defect.Defect;
import de.oppermann.maven.pflist.property.PFProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * User: sop
 * Date: 03.05.11
 * Time: 13:05
 */
public class CheckPropertyDuplicateInPropertyFile implements Check {

    PFProperties pfProperties;

    public CheckPropertyDuplicateInPropertyFile(PFProperties pfProperties) {
        this.pfProperties = pfProperties;
    }

    public List<Defect> checkForErrors() {
        List<Defect> defects = new ArrayList<Defect>();

        defects.addAll(pfProperties.checkForDuplicateEntry());

        return defects;
    }
}
