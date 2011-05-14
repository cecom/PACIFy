package de.oppermann.maven.pflist.defect;

import de.oppermann.maven.pflist.xml.PFList;
import de.oppermann.maven.pflist.xml.PFListProperty;

/**
 * Created by IntelliJ IDEA.
 * User: sop
 * Date: 02.05.11
 * Time: 10:49
 */
public class PropertyNotDefinedDefect implements Defect {

    private PFList pfList;
    private PFListProperty pfListProperty;

    public PropertyNotDefinedDefect(PFList pfList, PFListProperty pfListProperty) {
        this.pfList = pfList;
        this.pfListProperty = pfListProperty;
    }

    public String getDefectMessage() {
        return "PFProperty [" + pfListProperty.getId() + "] which is defined in [" + pfList.getFile().getPath() + "] is not set.";
    }

}

