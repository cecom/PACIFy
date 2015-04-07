package com.geewhiz.pacify.defect;

import com.geewhiz.pacify.model.PFListEntity;
import com.geewhiz.pacify.model.PFPropertyEntity;
import com.geewhiz.pacify.property.PropertyContainer;

/**
 * Created by IntelliJ IDEA.
 * User: sop
 * Date: 02.05.11
 * Time: 10:49
 */
public class PropertyNotDefinedDefect implements Defect {

    private PFListEntity pfListEntity;
    private PFPropertyEntity pfPropertyEntity;
    private PropertyContainer propertyContainer;

    public PropertyNotDefinedDefect(PFListEntity pfListEntity, PFPropertyEntity pfPropertyEntity, PropertyContainer propertyContainer) {
        this.pfListEntity = pfListEntity;
        this.pfPropertyEntity = pfPropertyEntity;
        this.propertyContainer = propertyContainer;
    }

    public String getDefectMessage() {
        return "Property [" + pfPropertyEntity.getId() + "] which is defined in [" + pfListEntity.getFile().getPath() + "] is not set in [" + propertyContainer.getPropertyLoadedFrom() + "].";
    }

}

