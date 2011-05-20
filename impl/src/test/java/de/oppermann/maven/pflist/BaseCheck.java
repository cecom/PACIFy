package de.oppermann.maven.pflist;

import de.oppermann.maven.pflist.checker.PFListCheck;
import de.oppermann.maven.pflist.defect.Defect;
import de.oppermann.maven.pflist.model.PFListEntity;
import de.oppermann.maven.pflist.model.PFEntityManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * User: sop
 * Date: 03.05.11
 * Time: 13:05
 */
public abstract class BaseCheck {

    protected List<Defect> getDefects(PFListCheck checker, File testStartPath) {
        PFEntityManager pfEntityManager = new PFEntityManager(testStartPath);

        List<Defect> defects = new ArrayList<Defect>();
        for (PFListEntity pfListEntity : pfEntityManager.getPFLists()) {
            defects.addAll(checker.checkForErrors(pfListEntity));
        }
        return defects;
    }


}
