package de.oppermann.maven.pflist.property;

import de.oppermann.maven.pflist.defect.Defect;

import java.util.List;
import java.util.Properties;

/**
 * User: sop
 * Date: 14.05.11
 * Time: 09:31
 */
public interface PropertyContainer {

    boolean containsKey(String key);

    String getPropertyValue(String key);

    Properties getProperties();

    List<Defect> checkForDuplicateEntry();

    String getPropertyLoadedFrom();
}
