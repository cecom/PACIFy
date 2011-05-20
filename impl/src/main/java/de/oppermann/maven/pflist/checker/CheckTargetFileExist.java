package de.oppermann.maven.pflist.checker;

import de.oppermann.maven.pflist.defect.Defect;
import de.oppermann.maven.pflist.defect.TargetFileDoesNotExistDefect;
import de.oppermann.maven.pflist.model.PFFileEntity;
import de.oppermann.maven.pflist.model.PFListEntity;
import de.oppermann.maven.pflist.model.PFPropertyEntity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * User: sop
 * Date: 03.05.11
 * Time: 13:05
 */
public class CheckTargetFileExist implements PFListCheck {

    public List<Defect> checkForErrors(PFListEntity pfListEntity) {
        List<Defect> defects = new ArrayList<Defect>();

        for (PFPropertyEntity pfPropertyEntity : pfListEntity.getPfPropertyEntities()) {
            for (PFFileEntity pfFileEntity : pfPropertyEntity.getPFFileEntities()) {
                File file = pfListEntity.getAbsoluteFileFor(pfFileEntity);
                if (file.exists() && file.isFile())
                    continue;
                Defect defect = new TargetFileDoesNotExistDefect(pfListEntity, pfPropertyEntity, pfFileEntity);
                defects.add(defect);
            }
        }

        return defects;
    }
}
