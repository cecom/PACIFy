package de.oppermann.maven.pflist.xml;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

/**
 * User: sop
 * Date: 03.05.11
 * Time: 13:05
 */

@Root(name = "file")
public class PFFile {

    @Attribute(name = "relative_path")
    private String relativePath;

    public String getRelativePath() {
        return relativePath;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PFFile pfFile = (PFFile) o;

        if (relativePath != null ? !relativePath.equals(pfFile.relativePath) : pfFile.relativePath != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return relativePath != null ? relativePath.hashCode() : 0;
    }
}
