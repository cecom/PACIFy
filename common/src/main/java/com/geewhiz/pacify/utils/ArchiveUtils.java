/*-
 * ========================LICENSE_START=================================
 * com.geewhiz.pacify.common
 * %%
 * Copyright (C) 2011 - 2017 Sven Oppermann
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */

package com.geewhiz.pacify.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.changes.ChangeSet;
import org.apache.commons.compress.changes.ChangeSetPerformer;
import org.apache.commons.io.IOUtils;

import com.geewhiz.pacify.defect.DefectRuntimeException;
import com.geewhiz.pacify.model.PArchive;
import com.geewhiz.pacify.model.PFile;
import com.geewhiz.pacify.model.utils.ModelUtils;

public class ArchiveUtils {

    public static boolean isArchiveAndIsSupported(String archiveName) {
        String type = getArchiveType(archiveName);

        if ("jar".equalsIgnoreCase(type)) {
            return true;
        }
        if ("zip".equalsIgnoreCase(type)) {
            return true;
        }
        if ("tar".equalsIgnoreCase(type)) {
            return true;
        }
        return false;
    }

    public static String getArchiveType(File archive) {
        return getArchiveType(archive.getName());
    }

    public static String getArchiveType(PArchive archive) {
        return getArchiveType(archive.getRelativePath());
    }

    public static String getArchiveType(String archiveName) {
        int idx = archiveName.lastIndexOf(".");
        String type = archiveName.substring(idx + 1);

        if ("jar".equalsIgnoreCase(type)) {
            return ArchiveStreamFactory.JAR;
        }
        if ("war".equalsIgnoreCase(type)) {
            return ArchiveStreamFactory.JAR;
        }
        if ("ear".equalsIgnoreCase(type)) {
            return ArchiveStreamFactory.JAR;
        }
        if ("zip".equalsIgnoreCase(type)) {
            return ArchiveStreamFactory.ZIP;
        }
        if ("tar".equalsIgnoreCase(type)) {
            return ArchiveStreamFactory.TAR;
        }

        return type;
    }

    public static File extractFile(File archive, String archiveType, String file) {

        Map<String, File> result = extractFiles(archive, archiveType, file, false);
        if (result.size() == 0)
            return null;
        if (result.size() > 1)
            throw new DefectRuntimeException("We got more than one File for [" + file + "] in archive [" + archive.getName() + "].");
        return result.get(file);
    }

    public static Map<String, File> extractFilesForRegExp(File archive, String archiveType, String regexpToMatch) {
        return extractFiles(archive, archiveType, regexpToMatch, true);
    }

    /**
     * 
     * @param archiveFile
     * @param archiveType
     * @param pFile
     * @return if pfile is a regular expression, a list of pfiles otherwise only one list entry.
     */
    public static List<PFile> extractPFile(PFile pFile) {
        if (pFile.getFile() != null) {
            List<PFile> result = new ArrayList<PFile>();
            result.add(pFile);
            return result;
        }

        if (pFile.isUseRegExResolution()) {
            return extractUsingRegExp(pFile);
        } else {
            return extractUsingRelativePath(pFile);
        }
    }

    private static List<PFile> extractUsingRelativePath(PFile pFile) {
        PArchive pArchive = pFile.getPArchive();
        File file = extractFile(pArchive.getFile(), pArchive.getType(), pFile.getRelativePath());
        pFile.setFile(file);

        List<PFile> result = new ArrayList<PFile>();
        result.add(pFile);
        return result;
    }

    private static List<PFile> extractUsingRegExp(PFile pFile) {
        List<PFile> result = new ArrayList<PFile>();
        PArchive pArchive = pFile.getPArchive();

        Map<String, File> files = extractFilesForRegExp(pArchive.getFile(), pArchive.getType(), pFile.getRelativePath());
        for (String relativePath : files.keySet()) {
            PFile aClone = ModelUtils.clonePFile(pFile, relativePath, files.get(relativePath));
            result.add(aClone);
        }

        // if we can't resolve the regular expression, return the given pfile
        if (result.size() == 0) {
            result.add(pFile);
        }
        return result;
    }

    public static File extractPArchive(PArchive pArchive) {
        if (pArchive.getFile() != null) {
            return pArchive.getFile();
        }

        PArchive parentArchive = pArchive.getParentArchive();
        return extractFile(parentArchive.getFile(), parentArchive.getType(), pArchive.getRelativePath());
    }

    public static void replaceFilesInArchives(List<PFile> replacePFiles) {
        // for performance, get all pfiles of the same archive
        Map<PArchive, List<PFile>> pFilesToReplace = getPFilesToReplace(replacePFiles);

        // replace the pfiles
        for (PArchive pArchive : pFilesToReplace.keySet()) {
            List<PFile> pFiles = pFilesToReplace.get(pArchive);
            replacePFilesInArchive(pArchive, pFiles);
            for (PFile toDelete : pFiles) {
                // delete the temporary extracted file
                toDelete.getFile().delete();
            }
        }

        // if we have an archive in archive, replace it. Order is relevant, so we use a LinkedHashMap pfile -> parchive3 -> parchive2 -> parchive1
        LinkedHashMap<PArchive, List<PArchive>> parentArchives = getParentArchives(replacePFiles);

        // replace the archives within the parent archive
        for (PArchive parentArchive : parentArchives.keySet()) {
            List<PArchive> pArchives = parentArchives.get(parentArchive);
            replacePArchivesInArchive(parentArchive, pArchives);
            for (PArchive pArchive : pArchives) {
                // delete the temporary extracted file
                pArchive.getFile().delete();
            }
        }
    }

    public static void replaceFilesInArchive(File archive, String archiveType, Map<String, File> filesToReplace) {
        ArchiveStreamFactory factory = new ArchiveStreamFactory();

        File manifest = null;
        InputStream archiveStream = null;
        ArchiveInputStream ais = null;
        ArchiveOutputStream aos = null;
        List<FileInputStream> streamsToClose = new ArrayList<FileInputStream>();

        File tmpArchive = FileUtils.createEmptyFileWithSamePermissions(archive);

        try {
            aos = factory.createArchiveOutputStream(archiveType, new FileOutputStream(tmpArchive));
            ChangeSet changes = new ChangeSet();

            if (ArchiveStreamFactory.JAR.equalsIgnoreCase(archiveType)) {
                manifest = manifestWorkaround(archive, archiveType, aos, changes, streamsToClose);
            }

            for (String filePath : filesToReplace.keySet()) {
                File replaceWithFile = filesToReplace.get(filePath);

                ArchiveEntry archiveEntry = aos.createArchiveEntry(replaceWithFile, filePath);
                FileInputStream fis = new FileInputStream(replaceWithFile);
                streamsToClose.add(fis);
                changes.add(archiveEntry, fis, true);
            }

            archiveStream = new FileInputStream(archive);
            ais = factory.createArchiveInputStream(archiveType, archiveStream);

            ChangeSetPerformer performer = new ChangeSetPerformer(changes);
            performer.perform(ais, aos);

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ArchiveException e) {
            throw new RuntimeException(e);
        } finally {
            for (FileInputStream fis : streamsToClose) {
                IOUtils.closeQuietly(fis);
            }
            IOUtils.closeQuietly(aos);
            IOUtils.closeQuietly(ais);
            IOUtils.closeQuietly(archiveStream);
        }

        if (manifest != null) {
            manifest.delete();
        }

        if (!archive.delete()) {
            throw new RuntimeException("Couldn't delete file [" + archive.getPath() + "]... Aborting!");
        }
        if (!tmpArchive.renameTo(archive)) {
            throw new RuntimeException("Couldn't rename filtered file from [" + tmpArchive.getPath() + "] to [" + archive.getPath() + "]... Aborting!");
        }
    }

    ////////////////////////////// PRIVAT Stuff
    private static Map<String, File> extractFiles(File archive, String archiveType, String searchFor, Boolean isRegExp) {
        Map<String, File> result = new HashMap<String, File>();

        ArchiveInputStream ais = null;
        try {
            ArchiveStreamFactory factory = new ArchiveStreamFactory();

            ais = factory.createArchiveInputStream(archiveType, new FileInputStream(archive));

            ArchiveEntry entry;
            while ((entry = ais.getNextEntry()) != null) {
                if (isRegExp) {
                    if (!matches(entry.getName(), searchFor)) {
                        continue;
                    }
                } else if (!searchFor.equals(entry.getName())) {
                    continue;
                }

                File physicalFile = FileUtils.createEmptyFileWithSamePermissions(archive,
                        archive.getName() + "!" + Paths.get(entry.getName()).getFileName().toString() + "_");

                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(physicalFile));

                byte[] content = new byte[2048];

                int len;
                while ((len = ais.read(content)) != -1) {
                    bos.write(content, 0, len);
                }

                bos.close();
                content = null;

                result.put(entry.getName(), physicalFile);
            }
        } catch (ArchiveException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(ais);
        }

        return result;
    }

    private static Boolean archiveContainsFile(File archive, String archiveType, String fileToLookFor) {
        ArchiveInputStream ais = null;
        try {
            ArchiveStreamFactory factory = new ArchiveStreamFactory();

            ais = factory.createArchiveInputStream(archiveType, new FileInputStream(archive));

            ArchiveEntry entry;
            while ((entry = ais.getNextEntry()) != null) {
                if (fileToLookFor.equals(entry.getName())) {
                    return Boolean.TRUE;
                }
            }

            return Boolean.FALSE;
        } catch (ArchiveException e) {
            throw new RuntimeException(e);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(ais);
        }
    }

    // if the archive contains a manifest, we have to simulate a change, otherwise
    // the manifest isn't the first entry anymore.
    private static File manifestWorkaround(File archive, String archiveType, ArchiveOutputStream aos, ChangeSet changes, List<FileInputStream> streamsToClose)
            throws IOException {
        String manifestPath = "META-INF/MANIFEST.MF";

        if (!archiveContainsFile(archive, archiveType, manifestPath)) {
            return null;
        }

        File originalManifestFile = extractFile(archive, archiveType, manifestPath);

        ArchiveEntry archiveEntry = aos.createArchiveEntry(originalManifestFile, manifestPath);
        FileInputStream fis = new FileInputStream(originalManifestFile);
        streamsToClose.add(fis);
        changes.add(archiveEntry, fis, true);

        return originalManifestFile;
    }

    private static Boolean matches(String pathName, String regEx) {
        Path path = FileSystems.getDefault().getPath(pathName);
        return FileUtils.matches(path, regEx);
    }

    private static Map<PArchive, List<PFile>> getPFilesToReplace(List<PFile> replacePFiles) {
        Map<PArchive, List<PFile>> pFilesToReplace = new HashMap<PArchive, List<PFile>>();
        for (PFile pFile : replacePFiles) {
            if (!pFile.isArchiveFile()) {
                continue;
            }
            PArchive pArchive = pFile.getPArchive();
            if (!pFilesToReplace.containsKey(pArchive)) {
                pFilesToReplace.put(pArchive, new ArrayList<PFile>());
            }
            List<PFile> pFiles = pFilesToReplace.get(pArchive);
            pFiles.add(pFile);
        }
        return pFilesToReplace;
    }

    private static LinkedHashMap<PArchive, List<PArchive>> getParentArchives(List<PFile> replacePFiles) {
        LinkedHashMap<PArchive, List<PArchive>> parentArchives = new LinkedHashMap<PArchive, List<PArchive>>();
        // for performance get first all archives in an archive
        for (PFile pFile : replacePFiles) {
            if (!pFile.isArchiveFile()) {
                continue;
            }
            PArchive pArchive = pFile.getPArchive();
            if (!pArchive.isArchiveFile()) {
                continue;
            }
            if (!parentArchives.containsKey(pArchive.getParentArchive())) {
                parentArchives.put(pArchive.getParentArchive(), new ArrayList<PArchive>());
            }
            List<PArchive> pArchivesToReplace = parentArchives.get(pArchive.getParentArchive());
            if (!pArchivesToReplace.contains(pArchive)) {
                pArchivesToReplace.add(pArchive);
            }
        }
        return parentArchives;
    }

    private static void replacePFilesInArchive(PArchive pArchive, List<PFile> replaceFiles) {

        Map<String, File> filesToReplace = new HashMap<String, File>();

        for (PFile pFile : replaceFiles) {
            String filePath = pFile.getRelativePath();
            File replaceWithFile = pFile.getFile();

            filesToReplace.put(filePath, replaceWithFile);
        }

        replaceFilesInArchive(pArchive.getFile(), pArchive.getType(), filesToReplace);
    }

    private static void replacePArchivesInArchive(PArchive parentArchive, List<PArchive> replaceFiles) {
        Map<String, File> filesToReplace = new HashMap<String, File>();

        for (PArchive pArchive : replaceFiles) {
            String filePath = pArchive.getRelativePath();
            File replaceWithFile = pArchive.getFile();

            filesToReplace.put(filePath, replaceWithFile);
        }

        replaceFilesInArchive(parentArchive.getFile(), parentArchive.getType(), filesToReplace);
    }

}
