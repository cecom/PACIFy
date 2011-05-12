package de.oppermann.maven.pflist.checker;

import de.oppermann.maven.pflist.defect.Defect;

import java.util.List;

/**
 * User: sop
 * Date: 03.05.11
 * Time: 13:05
 */
public interface Check {
    /**
     * @return if there are defects, return a list with the defects.
     */
    List<Defect> checkForErrors();
}
