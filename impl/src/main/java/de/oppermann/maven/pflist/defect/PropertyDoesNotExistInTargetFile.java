package de.oppermann.maven.pflist.defect;

import de.oppermann.maven.pflist.model.PFFileEntity;
import de.oppermann.maven.pflist.model.PFListEntity;
import de.oppermann.maven.pflist.model.PFPropertyEntity;

/**
 * User: sop
 * Date: 03.05.11
 * Time: 13:05
 */
public class PropertyDoesNotExistInTargetFile implements Defect {

    private PFListEntity pfListEntity;
    private PFPropertyEntity pfPropertyEntity;
    private PFFileEntity pfFileEntity;

    public PropertyDoesNotExistInTargetFile(PFListEntity pfListEntity, PFPropertyEntity pfPropertyEntity, PFFileEntity pfFileEntity) {
        this.pfListEntity = pfListEntity;
        this.pfPropertyEntity = pfPropertyEntity;
        this.pfFileEntity = pfFileEntity;
    }

    public String getDefectMessage() {
        return "Property [" + pfPropertyEntity.getId() + "] which is defined in [" + pfListEntity.getFile().getPath()
                + "] couldn't be found in file [" + pfListEntity.getAbsoluteFileFor(pfFileEntity).getPath() + "]";
    }
}