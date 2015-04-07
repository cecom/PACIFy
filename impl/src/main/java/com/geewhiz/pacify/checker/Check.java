package com.geewhiz.pacify.checker;


import java.util.List;

import com.geewhiz.pacify.defect.Defect;

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
