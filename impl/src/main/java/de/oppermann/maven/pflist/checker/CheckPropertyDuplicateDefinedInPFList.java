package de.oppermann.maven.pflist.checker;

import de.oppermann.maven.pflist.defect.Defect;
import de.oppermann.maven.pflist.defect.PropertyDuplicateDefinedInPFList;
import de.oppermann.maven.pflist.model.PFListEntity;
import de.oppermann.maven.pflist.model.PFPropertyEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * User: sop
 * Date: 03.05.11
 * Time: 13:05
 */
public class CheckPropertyDuplicateDefinedInPFList implements PFListCheck {

    public List<Defect> checkForErrors(PFListEntity pfListEntity) {
        List<Defect> defects = new ArrayList<Defect>();

        List<String> propertyIds = new ArrayList<String>();

        for (PFPropertyEntity pfPropertyEntity : pfListEntity.getPfPropertyEntities()) {
            if (propertyIds.contains(pfPropertyEntity.getId())) {
                Defect defect = new PropertyDuplicateDefinedInPFList(pfListEntity, pfPropertyEntity);
                defects.add(defect);
                continue;
            }
            propertyIds.add(pfPropertyEntity.getId());
        }
        return defects;
    }
}
