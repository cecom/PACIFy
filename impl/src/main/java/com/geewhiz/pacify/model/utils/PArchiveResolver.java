package com.geewhiz.pacify.model.utils;

import java.util.ArrayList;
import java.util.List;

import com.geewhiz.pacify.model.PArchive;
import com.geewhiz.pacify.model.PFile;
import com.geewhiz.pacify.utils.FileUtils;

public class PArchiveResolver {

    PArchive pArchive;

    public PArchiveResolver(PArchive pArchive) {
        this.pArchive = pArchive;
    }

    public List<PFile> resolve() {
        List<PFile> result = new ArrayList<PFile>();

        List<PFile> pFiles = pArchive.getPFiles();
        for (PFile pFile : pFiles) {
            List<PFile> extractedPFiles = FileUtils.extractFiles(pArchive.getFile(), pArchive.getType(), pFile);
            if (extractedPFiles != null)
                result.addAll(extractedPFiles);
        }

        return result;
    }

}
