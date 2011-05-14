package de.oppermann.maven.pflist.defect;

import de.oppermann.maven.pflist.xml.PFFile;
import de.oppermann.maven.pflist.xml.PFList;
import de.oppermann.maven.pflist.xml.PFListProperty;

/**
 * User: sop
 * Date: 03.05.11
 * Time: 13:05
 */
public class PropertyDoesNotExistInTargetFile implements Defect {

    private PFList pfList;
    private PFListProperty pfListProperty;
    private PFFile pfFile;

    public PropertyDoesNotExistInTargetFile(PFList pfList, PFListProperty pfListProperty, PFFile pfFile) {
        this.pfList = pfList;
        this.pfListProperty = pfListProperty;
        this.pfFile = pfFile;
    }

    public String getDefectMessage() {
        return "Property [" + pfListProperty.getId() + "] which is defined in [" + pfList.getFile().getPath()
                + "] couldn't be found in file [" + pfList.getAbsoluteFileFor(pfFile).getPath() + "]";
    }
}