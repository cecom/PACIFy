package de.oppermann.maven.pflist.defect;

import de.oppermann.maven.pflist.xml.PFList;
import de.oppermann.maven.pflist.xml.PFListProperty;

/**
 * User: sop
 * Date: 03.05.11
 * Time: 16:03
 */
public class PropertyDuplicateDefinedInPFList implements Defect {

    private PFList pfList;
    private PFListProperty pfListProperty;

    public PropertyDuplicateDefinedInPFList(PFList pfList, PFListProperty pfListProperty) {
        this.pfList = pfList;
        this.pfListProperty = pfListProperty;
    }

    public String getDefectMessage() {
        return "PFProperty [" + pfListProperty.getId() + "] is duplicate defined in [" + pfList.getFile().getPath() + "]";
    }
}
