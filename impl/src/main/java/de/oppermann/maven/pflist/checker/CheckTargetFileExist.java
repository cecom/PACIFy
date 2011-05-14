package de.oppermann.maven.pflist.checker;

import de.oppermann.maven.pflist.defect.Defect;
import de.oppermann.maven.pflist.defect.TargetFileDoesNotExistDefect;
import de.oppermann.maven.pflist.xml.PFFile;
import de.oppermann.maven.pflist.xml.PFList;
import de.oppermann.maven.pflist.xml.PFListProperty;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * User: sop
 * Date: 03.05.11
 * Time: 13:05
 */
public class CheckTargetFileExist implements PFListCheck {

    public List<Defect> checkForErrors(PFList pfList) {
        List<Defect> defects = new ArrayList<Defect>();
        for (PFListProperty pfListProperty : pfList.getPfListProperties()) {
            for (PFFile pfFile : pfListProperty.getPFFiles()) {
                File file = pfList.getAbsoluteFileFor(pfFile);
                if (file.exists() && file.isFile())
                    continue;
                Defect defect = new TargetFileDoesNotExistDefect(pfList, pfListProperty, pfFile);
                defects.add(defect);
            }
        }
        return defects;
    }
}
