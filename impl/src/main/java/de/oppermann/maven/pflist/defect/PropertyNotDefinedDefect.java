package de.oppermann.maven.pflist.defect;

import de.oppermann.maven.pflist.xml.PFList;
import de.oppermann.maven.pflist.xml.PFProperty;

import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: sop
 * Date: 02.05.11
 * Time: 10:49
 * To change this template use PFFile | Settings | PFFile Templates.
 */
public class PropertyNotDefinedDefect implements Defect {

    private PFList pfList;
    private PFProperty pfProperty;
    private File propertyFile;

    public PropertyNotDefinedDefect(PFList pfList, PFProperty pfProperty, File propertyFile) {
        this.pfList = pfList;
        this.pfProperty = pfProperty;
        this.propertyFile = propertyFile;
    }

    public String getDefectMessage() {
        return "PFProperty [" + pfProperty.getId() + "] which is defined in [" + pfList.getFile().getPath() + "] is not defined in your property file [" + propertyFile.getPath() + "].";
    }

}

