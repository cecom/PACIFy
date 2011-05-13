package de.oppermann.maven.pflist.checker;

import de.oppermann.maven.pflist.defect.Defect;
import de.oppermann.maven.pflist.property.PropertyFile;
import de.oppermann.maven.pflist.xml.PFList;

import java.util.ArrayList;
import java.util.List;

/**
 * User: sop
 * Date: 03.05.11
 * Time: 13:05
 */
public class PFListChecker {

    List<Check> checks = new ArrayList<Check>();
    List<PFListCheck> pfListChecks = new ArrayList<PFListCheck>();

    public PFListChecker(PropertyFile propertyFile) {
        checks.add(new CheckPropertyDuplicateInPropertyFile(propertyFile));
        pfListChecks.add(new CheckTargetFileExist());
        pfListChecks.add(new CheckPropertyDuplicateDefinedInPFList());
        pfListChecks.add(new CheckPropertyExists(propertyFile));
        pfListChecks.add(new CheckPropertyExistsInTargetFile());
    }

    public List<Defect> check(PFList pfList) {
        List<Defect> defects = new ArrayList<Defect>();
        for (Check check : checks) {
            defects.addAll(check.checkForErrors());
        }
        for (PFListCheck pfListCheck : pfListChecks) {
            defects.addAll(pfListCheck.checkForErrors(pfList));
        }
        return defects;
    }
}
