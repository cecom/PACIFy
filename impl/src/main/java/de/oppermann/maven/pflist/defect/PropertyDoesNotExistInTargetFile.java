package de.oppermann.maven.pflist.defect;

import de.oppermann.maven.pflist.xml.PFFile;
import de.oppermann.maven.pflist.xml.PFList;
import de.oppermann.maven.pflist.xml.PFProperty;

/**
 * User: sop
 * Date: 03.05.11
 * Time: 13:05
 */
public class PropertyDoesNotExistInTargetFile implements Defect {

    private PFList pfList;
    private PFProperty pfProperty;
    private PFFile pfFile;

    public PropertyDoesNotExistInTargetFile(PFList pfList, PFProperty pfProperty, PFFile pfFile) {
        this.pfList = pfList;
        this.pfProperty = pfProperty;
        this.pfFile = pfFile;
    }

    public String getDefectMessage() {
        return "Property [" + pfProperty.getId() + "] which is defined in [" + pfList.getFile().getPath()
                + "] couldn't be found in file [" + pfList.getAbsoluteFileFor(pfFile).getPath() + "]";
    }
}