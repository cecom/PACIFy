package de.oppermann.maven.pflist.xml;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * User: sop
 * Date: 03.05.11
 * Time: 13:05
 */
@Root(name = "property", strict = false)
public class PFListProperty {

    @Attribute
    private String id;

    @ElementList(name = "file", inline = true)
    private List<PFFile> pfFiles;


    public String getId() {
        return id;
    }

    public List<PFFile> getPFFiles() {
        return pfFiles;
    }
}
