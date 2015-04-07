package com.geewhiz.pacify.checker;


import java.util.ArrayList;
import java.util.List;

import com.geewhiz.pacify.defect.Defect;
import com.geewhiz.pacify.defect.PropertyNotDefinedDefect;
import com.geewhiz.pacify.model.PFListEntity;
import com.geewhiz.pacify.model.PFPropertyEntity;
import com.geewhiz.pacify.property.PropertyContainer;

/**
 * User: sop
 * Date: 03.05.11
 * Time: 13:05
 */
public class CheckPropertyExists implements PFListCheck {

    private PropertyContainer propertyContainer;

    public CheckPropertyExists(PropertyContainer propertyContainer) {
        this.propertyContainer = propertyContainer;
    }

    public List<Defect> checkForErrors(PFListEntity pfListEntity) {
        List<Defect> defects = new ArrayList<Defect>();

        List<PFPropertyEntity> pfPropertyEntities = pfListEntity.getPfPropertyEntities();
        for (PFPropertyEntity pfPropertyEntity : pfPropertyEntities) {
            if (propertyContainer.containsKey(pfPropertyEntity.getId()))
                continue;
            Defect defect = new PropertyNotDefinedDefect(pfListEntity, pfPropertyEntity, propertyContainer);
            defects.add(defect);
        }

        return defects;
    }
}
