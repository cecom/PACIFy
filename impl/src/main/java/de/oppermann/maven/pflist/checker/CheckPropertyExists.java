package de.oppermann.maven.pflist.checker;

import de.oppermann.maven.pflist.defect.Defect;
import de.oppermann.maven.pflist.defect.PropertyNotDefinedDefect;
import de.oppermann.maven.pflist.property.PropertyFile;
import de.oppermann.maven.pflist.xml.PFList;
import de.oppermann.maven.pflist.xml.PFProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * User: sop
 * Date: 03.05.11
 * Time: 13:05
 */
public class CheckPropertyExists implements PFListCheck {

    private PropertyFile propertyFile;

    public CheckPropertyExists(PropertyFile propertyFile) {
        this.propertyFile = propertyFile;
    }

    public List<Defect> checkForErrors(PFList pfList) {
        List<Defect> defects = new ArrayList<Defect>();
        List<PFProperty> pfProperties = pfList.getPfProperties();
        for (PFProperty pfProperty : pfProperties) {
            if (propertyFile.hasProperty(pfProperty.getId()))
                continue;
            Defect defect = new PropertyNotDefinedDefect(pfList, pfProperty);
            defects.add(defect);
        }
        return defects;
    }
}
