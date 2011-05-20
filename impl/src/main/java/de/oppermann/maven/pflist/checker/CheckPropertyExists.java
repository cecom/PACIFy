package de.oppermann.maven.pflist.checker;

import de.oppermann.maven.pflist.defect.Defect;
import de.oppermann.maven.pflist.defect.PropertyNotDefinedDefect;
import de.oppermann.maven.pflist.model.PFPropertyEntity;
import de.oppermann.maven.pflist.property.PropertyContainer;
import de.oppermann.maven.pflist.model.PFListEntity;

import java.util.ArrayList;
import java.util.List;

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
