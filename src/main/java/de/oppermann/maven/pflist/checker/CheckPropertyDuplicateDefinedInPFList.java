package de.oppermann.maven.pflist.checker;

import de.oppermann.maven.pflist.defect.Defect;
import de.oppermann.maven.pflist.defect.PropertyDuplicateDefinedInPFList;
import de.oppermann.maven.pflist.xml.PFList;
import de.oppermann.maven.pflist.xml.PFProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * User: sop
 * Date: 03.05.11
 * Time: 13:05
 */
public class CheckPropertyDuplicateDefinedInPFList implements PFListCheck {

    public CheckPropertyDuplicateDefinedInPFList() {
    }

    public List<Defect> checkForErrors(PFList pfList) {
        List<Defect> defects = new ArrayList<Defect>();
        List<String> propertyIds = new ArrayList<String>();

        for (PFProperty pfProperty : pfList.getPfProperties()) {
            if (propertyIds.contains(pfProperty.getId())) {
                Defect defect = new PropertyDuplicateDefinedInPFList(pfList, pfProperty);
                defects.add(defect);
                continue;
            }
            propertyIds.add(pfProperty.getId());
        }
        return defects;
    }
}
