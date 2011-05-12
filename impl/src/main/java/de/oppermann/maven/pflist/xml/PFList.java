package de.oppermann.maven.pflist.xml;

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
public class PFList {

    @ElementList(name = "property", inline = true)
    private List<PFProperty> pfProperties;

    @Transient
    private File file;

    public List<PFProperty> getPfProperties() {
        return pfProperties;
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

    public File getAbsoluteFileFor(PFFile pfFile) {
        return new File(getFolder(), pfFile.getRelativePath());
    }

    public List<PFFile> getPfFiles() {
        List<PFFile> result = new ArrayList<PFFile>();
        for (PFProperty pfProperty : getPfProperties()) {
            for (PFFile pfFile : pfProperty.getPFFiles()) {
                if (result.contains(pfFile))
                    continue;
                result.add(pfFile);
            }
        }
        return result;
    }

    public List<PFProperty> getPfPropertiesForPfFile(PFFile pfFile) {
        List<PFProperty> result = new ArrayList<PFProperty>();
        for (PFProperty pfProperty : getPfProperties())
            if (pfProperty.getPFFiles().contains(pfFile))
                result.add(pfProperty);
        return result;
    }
}
