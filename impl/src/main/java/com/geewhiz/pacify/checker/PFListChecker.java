package com.geewhiz.pacify.checker;


import java.util.ArrayList;
import java.util.List;

import com.geewhiz.pacify.defect.Defect;
import com.geewhiz.pacify.model.PFListEntity;
import com.geewhiz.pacify.property.PropertyContainer;

/**
 * User: sop
 * Date: 03.05.11
 * Time: 13:05
 */
public class PFListChecker {

    List<Check> checks = new ArrayList<Check>();
    List<PFListCheck> pfListChecks = new ArrayList<PFListCheck>();

    public PFListChecker(PropertyContainer propertyContainer) {
        checks.add(new CheckPropertyDuplicateInPropertyFile(propertyContainer));
        pfListChecks.add(new CheckTargetFileExist());
        pfListChecks.add(new CheckPropertyDuplicateDefinedInPFList());
        pfListChecks.add(new CheckPropertyExists(propertyContainer));
        pfListChecks.add(new CheckPropertyExistsInTargetFile());
    }

    public List<Defect> check(PFListEntity pfListEntity) {
        List<Defect> defects = new ArrayList<Defect>();
        for (Check check : checks) {
            defects.addAll(check.checkForErrors());
        }
        for (PFListCheck pfListCheck : pfListChecks) {
            defects.addAll(pfListCheck.checkForErrors(pfListEntity));
        }
        return defects;
    }
}
