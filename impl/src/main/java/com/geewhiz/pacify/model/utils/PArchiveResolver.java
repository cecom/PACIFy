package com.geewhiz.pacify.model.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.NotImplementedException;

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

        if (pArchive.getFile() == null) {
            File physicalFile = null;
            if (pArchive.isArchiveFile()) {
                physicalFile = FileUtils.extractPArchive(pArchive);
            } else {
                physicalFile = new File(pArchive.getPMarker().getFolder(), pArchive.getRelativePath());
            }
            pArchive.setFile(physicalFile);
        }

        for (Object entry : pArchive.getFilesAndArchives()) {
            if (entry instanceof PFile) {
                PFile pFile = (PFile) entry;
                result.addAll(resolvePFiles(pFile));
            } else if (entry instanceof PArchive) {
                PArchive pArchive = (PArchive) entry;
                PArchiveResolver resolver = new PArchiveResolver(pArchive);
                result.addAll(resolver.resolve());
            } else {
                throw new NotImplementedException("Type not implemented [" + entry.getClass() + "]");
            }

        }
        return result;
    }

    private List<PFile> resolvePFiles(PFile pFile) {
        List<PFile> result = new ArrayList<PFile>();

        List<PFile> extractedPFiles = FileUtils.extractPFile(pFile);
        if (extractedPFiles != null) {
            result.addAll(extractedPFiles);
        }

        return result;
    }

}
