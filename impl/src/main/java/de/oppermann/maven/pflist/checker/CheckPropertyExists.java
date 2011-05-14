package de.oppermann.maven.pflist.checker;

import de.oppermann.maven.pflist.defect.Defect;
import de.oppermann.maven.pflist.defect.PropertyNotDefinedDefect;
import de.oppermann.maven.pflist.property.PFProperties;
import de.oppermann.maven.pflist.xml.PFList;
import de.oppermann.maven.pflist.xml.PFListProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * User: sop
 * Date: 03.05.11
 * Time: 13:05
 */
public class CheckPropertyExists implements PFListCheck {

    private PFProperties pfProperties;

    public CheckPropertyExists(PFProperties pfProperties) {
        this.pfProperties = pfProperties;
    }

    public List<Defect> checkForErrors(PFList pfList) {
        List<Defect> defects = new ArrayList<Defect>();
        List<PFListProperty> pfListProperties = pfList.getPfListProperties();
        for (PFListProperty pfListListProperty : pfListProperties) {
            if (pfProperties.contains(pfListListProperty.getId()))
                continue;
            Defect defect = new PropertyNotDefinedDefect(pfList, pfListListProperty);
            defects.add(defect);
        }
        return defects;
    }
}
