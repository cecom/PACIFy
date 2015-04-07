package com.geewhiz.pacify.checker;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.geewhiz.pacify.defect.Defect;
import com.geewhiz.pacify.defect.TargetFileDoesNotExistDefect;
import com.geewhiz.pacify.model.PFFileEntity;
import com.geewhiz.pacify.model.PFListEntity;
import com.geewhiz.pacify.model.PFPropertyEntity;

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
