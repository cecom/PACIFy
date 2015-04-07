package com.geewhiz.pacify.model;

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
public class PFPropertyEntity {

    @Attribute
    private String id;

    @ElementList(name = "file", inline = true)
    private List<PFFileEntity> pfFileEntities;

    @Attribute(name = "convertBackslashToSlash", required = false, empty = "false")
    private String convertBackslashToSlash;

    public String getId() {
        return id;
    }

    public List<PFFileEntity> getPFFileEntities() {
        return pfFileEntities;
    }

    public Boolean convertBackslashToSlash() {
        return Boolean.parseBoolean(convertBackslashToSlash);
    }

}
