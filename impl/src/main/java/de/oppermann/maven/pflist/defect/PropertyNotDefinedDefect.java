package de.oppermann.maven.pflist.defect;

import de.oppermann.maven.pflist.xml.PFList;
import de.oppermann.maven.pflist.xml.PFProperty;

/**
 * Created by IntelliJ IDEA.
 * User: sop
 * Date: 02.05.11
 * Time: 10:49
 */
public class PropertyNotDefinedDefect implements Defect {

    private PFList pfList;
    private PFProperty pfProperty;

    public PropertyNotDefinedDefect(PFList pfList, PFProperty pfProperty) {
        this.pfList = pfList;
        this.pfProperty = pfProperty;
    }

    public String getDefectMessage() {
        return "PFProperty [" + pfProperty.getId() + "] which is defined in [" + pfList.getFile().getPath() + "] is not set in your property file.";
    }

}

