package com.geewhiz.pacify.defect;

import com.geewhiz.pacify.model.PFListEntity;
import com.geewhiz.pacify.model.PFPropertyEntity;

/**
 * User: sop
 * Date: 03.05.11
 * Time: 16:03
 */
public class PropertyDuplicateDefinedInPFList implements Defect {

    private PFListEntity pfListEntity;
    private PFPropertyEntity pfPropertyEntity;

    public PropertyDuplicateDefinedInPFList(PFListEntity pfListEntity, PFPropertyEntity pfPropertyEntity) {
        this.pfListEntity = pfListEntity;
        this.pfPropertyEntity = pfPropertyEntity;
    }

    public String getDefectMessage() {
        return "Property [" + pfPropertyEntity.getId() + "] is duplicate defined in pflist descriptor [" + pfListEntity.getFile().getPath() + "]";
    }
}
