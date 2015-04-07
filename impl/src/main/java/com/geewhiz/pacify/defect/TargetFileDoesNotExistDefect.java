package com.geewhiz.pacify.defect;

import com.geewhiz.pacify.model.PFFileEntity;
import com.geewhiz.pacify.model.PFListEntity;
import com.geewhiz.pacify.model.PFPropertyEntity;

/**
 * User: sop
 * Date: 03.05.11
 * Time: 13:05
 */
public class TargetFileDoesNotExistDefect implements Defect {

    private PFListEntity pfListEntity;
    private PFPropertyEntity pfPropertyEntity;
    private PFFileEntity pfFileEntity;

    public TargetFileDoesNotExistDefect(PFListEntity pfListEntity, PFPropertyEntity pfPropertyEntity, PFFileEntity pfFileEntity) {
        this.pfListEntity = pfListEntity;
        this.pfPropertyEntity = pfPropertyEntity;
        this.pfFileEntity = pfFileEntity;
    }

    public String getDefectMessage() {
        return "File [" + pfListEntity.getAbsoluteFileFor(pfFileEntity).getPath() + "] which is defined in [" + pfListEntity.getFile().getPath()
                + "] property [" + pfPropertyEntity.getId() + "] " + "does not exist.";
    }

}
