package com.geewhiz.pacify;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.geewhiz.pacify.checker.PFListCheck;
import com.geewhiz.pacify.defect.Defect;
import com.geewhiz.pacify.model.PFEntityManager;
import com.geewhiz.pacify.model.PFListEntity;

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
