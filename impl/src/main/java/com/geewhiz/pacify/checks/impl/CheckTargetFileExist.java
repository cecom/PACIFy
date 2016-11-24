package com.geewhiz.pacify.checks.impl;

import java.util.LinkedHashSet;

import com.geewhiz.pacify.checks.PMarkerCheck;
import com.geewhiz.pacify.defect.Defect;
import com.geewhiz.pacify.defect.FileDoesNotExistDefect;
import com.geewhiz.pacify.managers.EntityManager;
import com.geewhiz.pacify.model.PFile;
import com.geewhiz.pacify.model.PMarker;

public class CheckTargetFileExist implements PMarkerCheck {

    public LinkedHashSet<Defect> checkForErrors(EntityManager entityManager, PMarker pMarker) {
        LinkedHashSet<Defect> defects = new LinkedHashSet<Defect>();

        for (PFile pFile : entityManager.getPFilesFrom(pMarker)) {
            if (!entityManager.doesFileExist(pFile)) {
                Defect defect = new FileDoesNotExistDefect(pFile);
                defects.add(defect);
            }
        }

        return defects;
    }

}