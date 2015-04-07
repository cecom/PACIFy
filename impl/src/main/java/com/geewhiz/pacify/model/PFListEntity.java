package com.geewhiz.pacify.model;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Transient;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * User: sop
 * Date: 03.05.11
 * Time: 13:05
 */

@Root(name = "property_file_list", strict = false)
public class PFListEntity {

    @ElementList(name = "property", inline = true)
    private List<PFPropertyEntity> pfPropertyEntities;

    @Transient
    private File file;

    public List<PFPropertyEntity> getPfPropertyEntities() {
        return pfPropertyEntities;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    public File getFolder() {
        return file.getParentFile();
    }

    public File getAbsoluteFileFor(PFFileEntity pfFileEntity) {
        return new File(getFolder(), pfFileEntity.getRelativePath());
    }

    public List<PFFileEntity> getPfFileEntities() {
        List<PFFileEntity> result = new ArrayList<PFFileEntity>();
        for (PFPropertyEntity pfPropertyEntity : getPfPropertyEntities()) {
            for (PFFileEntity pfFileEntity : pfPropertyEntity.getPFFileEntities()) {
                if (result.contains(pfFileEntity))
                    continue;
                result.add(pfFileEntity);
            }
        }
        return result;
    }

    public List<PFPropertyEntity> getPfPropertyEntitiesForPFFileEntity(PFFileEntity pfFileEntity) {
        List<PFPropertyEntity> result = new ArrayList<PFPropertyEntity>();
        for (PFPropertyEntity pfPropertyEntity : getPfPropertyEntities())
            if (pfPropertyEntity.getPFFileEntities().contains(pfFileEntity))
                result.add(pfPropertyEntity);
        return result;
    }
}
