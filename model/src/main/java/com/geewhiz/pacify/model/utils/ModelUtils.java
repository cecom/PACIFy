package com.geewhiz.pacify.model.utils;

import java.io.File;

import com.geewhiz.pacify.model.PFile;

public class ModelUtils {

    public static PFile createPFile(PFile cloneFrom, String relativePath, File physicalPath) {
        PFile aClone = (PFile) cloneFrom.clone();

        aClone.setFile(physicalPath);
        aClone.setUseRegExResolution(false);
        aClone.setRelativePath(relativePath);

        return aClone;
    }
}
