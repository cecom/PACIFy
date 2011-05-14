package de.oppermann.maven.pflist.defect;

import de.oppermann.maven.pflist.xml.PFFile;
import de.oppermann.maven.pflist.xml.PFList;
import de.oppermann.maven.pflist.xml.PFListProperty;

/**
 * User: sop
 * Date: 03.05.11
 * Time: 13:05
 */
public class TargetFileDoesNotExistDefect implements Defect {

    private PFList pfList;
    private PFListProperty pfListProperty;
    private PFFile pfFile;

    public TargetFileDoesNotExistDefect(PFList pfList, PFListProperty pfListProperty, PFFile pfFile) {
        this.pfList = pfList;
        this.pfListProperty = pfListProperty;
        this.pfFile = pfFile;
    }

    public String getDefectMessage() {
        return "File [" + pfList.getAbsoluteFileFor(pfFile).getPath() + "] which is defined in [" + pfList.getFile().getPath()
                + "] property [" + pfListProperty.getId() + "] " + "does not exist.";
    }

}
