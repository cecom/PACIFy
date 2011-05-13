package de.oppermann.maven.pflist;

import de.oppermann.maven.pflist.checker.PFListCheck;
import de.oppermann.maven.pflist.defect.Defect;
import de.oppermann.maven.pflist.property.PropertyFile;
import de.oppermann.maven.pflist.xml.PFList;
import de.oppermann.maven.pflist.xml.PFManager;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * User: sop
 * Date: 03.05.11
 * Time: 13:05
 */
public abstract class CheckTest {

    protected List<Defect> getDefects(PFListCheck checker, File testStartPath, PropertyFile propertyFile) {
        PFManager pfManager = new PFManager(testStartPath, propertyFile);

        List<Defect> defects = new ArrayList<Defect>();
        for (PFList pfList : pfManager.getPFLists()) {
            defects.addAll(checker.checkForErrors(pfList));
        }
        return defects;
    }


}
