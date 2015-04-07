package com.geewhiz.pacify.checker;


import java.util.List;

import com.geewhiz.pacify.defect.Defect;
import com.geewhiz.pacify.model.PFListEntity;

/**
 * User: sop
 * Date: 03.05.11
 * Time: 13:05
 */
public interface PFListCheck {

    /**
     * @param pfListEntity which pfList should be checked?
     * @return if there are defects, return a list with the defects.
     */
    List<Defect> checkForErrors(PFListEntity pfListEntity);
}
