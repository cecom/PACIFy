package de.oppermann.maven.pflist.defect;

import de.oppermann.maven.pflist.xml.PFList;
import de.oppermann.maven.pflist.xml.PFProperty;

/**
 * User: sop
 * Date: 03.05.11
 * Time: 16:03
 */
public class PropertyDuplicateDefinedInPFList implements Defect {

    private PFList pfList;
    private PFProperty pfProperty;

    public PropertyDuplicateDefinedInPFList(PFList pfList, PFProperty pfProperty) {
        this.pfList = pfList;
        this.pfProperty = pfProperty;
    }

    public String getDefectMessage() {
        return "PFProperty [" + pfProperty.getId() + "] is duplicate defined in [" + pfList.getFile().getPath() + "]";
    }
}
