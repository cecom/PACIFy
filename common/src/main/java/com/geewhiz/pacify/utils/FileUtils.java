package com.geewhiz.pacify.utils;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.changes.ChangeSet;
import org.apache.commons.compress.changes.ChangeSetPerformer;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;

import com.geewhiz.pacify.model.PArchive;
import com.geewhiz.pacify.model.PFile;
import com.geewhiz.pacify.model.utils.ModelUtils;

public class FileUtils {

    private static final boolean IS_POSIX = FileSystems.getDefault().supportedFileAttributeViews().contains("posix");

    public static List<String> getFileAsLines(URL fileURL, String encoding) {
        InputStream is = null;
        try {
            is = fileURL.openStream();
            return IOUtils.readLines(is, Charsets.toCharset(encoding));
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    public static URL getFileUrl(File file) {
        try {
            return file.toURI().toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public static URL getFileUrl(URL url, String file) {
        try {
            return new URL(url, file);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public static File createEmptyFileWithSamePermissions(File forFile) {
        return createEmptyFileWithSamePermissions(forFile, forFile.getName());
    }

    public static File createEmptyFileWithSamePermissions(File forFile, String filePrefix) {
        try {
            File folder = forFile.getParentFile();

            File tmp = File.createTempFile(filePrefix, ".tmp", folder);

            if (IS_POSIX) {
                Set<PosixFilePermission> attrs = Files.getPosixFilePermissions(Paths.get(forFile.toURI()));
                Files.setPosixFilePermissions(Paths.get(tmp.toURI()), attrs);
            }

            return tmp;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Boolean archiveContainsFile(PArchive pArchive, String fileToLookFor) {
        ArchiveInputStream ais = null;
        try {
            ArchiveStreamFactory factory = new ArchiveStreamFactory();

            ais = factory.createArchiveInputStream(pArchive.getType(), new FileInputStream(pArchive.getFile()));

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

    public static String getFileInOneString(File file, String encoding) {
        InputStream is = null;
        try {
            is = new FileInputStream(file);
            return IOUtils.toString(is, Charsets.toCharset(encoding));
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    /**
     * 
     * @param archiveFile
     * @param archiveType
     * @param pFile
     * @return if pfile is a regular expression, a list of pfiles.
     */
    public static List<PFile> extractPFile(PFile pFile) {
        PArchive pArchive = pFile.getPArchive();

        List<PFile> result = new ArrayList<PFile>();

        if (pFile.getFile() != null) {
            result.add(pFile);
            return result;
        }

        ArchiveInputStream ais = null;
        try {
            ArchiveStreamFactory factory = new ArchiveStreamFactory();

            ais = factory.createArchiveInputStream(pArchive.getType(), new FileInputStream(pArchive.getFile()));

            ArchiveEntry entry;
            while ((entry = ais.getNextEntry()) != null) {
                if (pFile.isUseRegExResolution()) {
                    if (!matches(entry.getName(), pFile.getRelativePath())) {
                        continue;
                    }
                } else if (!pFile.getRelativePath().equals(entry.getName())) {
                    continue;
                }

                File physicalFile = FileUtils.createEmptyFileWithSamePermissions(pArchive.getFile(),
                        pArchive.getFile().getName() + "!" + Paths.get(entry.getName()).getFileName().toString() + "_");

                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(physicalFile));

                byte[] content = new byte[2048];

                int len;
                while ((len = ais.read(content)) != -1) {
                    bos.write(content, 0, len);
                }

                bos.close();
                content = null;

                if (pFile.isUseRegExResolution()) {
                    PFile aClone = ModelUtils.createPFile(pFile, physicalFile.getName(), physicalFile);
                    result.add(aClone);
                } else {
                    pFile.setFile(physicalFile);
                    result.add(pFile);
                    break;
                }
            }
        } catch (ArchiveException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(ais);
        }

        // if we can't resolve the regular expression, return the given pfile
        if (result.size() == 0) {
            result.add(pFile);
            return result;
        }
        return result;
    }

    public static File extractPArchive(PArchive pArchive) {
        if (pArchive.getFile() != null) {
            return pArchive.getFile();
        }

        File result = null;

        PArchive parentArchive = pArchive.getParentArchive();

        ArchiveInputStream ais = null;
        try {
            ArchiveStreamFactory factory = new ArchiveStreamFactory();

            ais = factory.createArchiveInputStream(parentArchive.getType(), new FileInputStream(parentArchive.getFile()));

            ArchiveEntry entry;
            while ((entry = ais.getNextEntry()) != null) {
                if (!pArchive.getRelativePath().equals(entry.getName())) {
                    continue;
                }

                result = FileUtils.createEmptyFileWithSamePermissions(parentArchive.getFile(),
                        parentArchive.getFile().getName() + "!" + Paths.get(entry.getName()).getFileName().toString() + "_");

                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(result));

                byte[] content = new byte[2048];

                int len;
                while ((len = ais.read(content)) != -1) {
                    bos.write(content, 0, len);
                }

                bos.close();
                content = null;

                break;
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

    // TODO: to much archive stuff. refactore it to a own class

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

        // if we have an archive in archive, replace it. Order is relevant, so we use a LinkedHashMap pfile -> parchive -> parchive -> parchive
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

    private static void replacePFilesInArchive(PArchive pArchive, List<PFile> replaceFiles) {
        ArchiveStreamFactory factory = new ArchiveStreamFactory();

        File manifest = null;
        InputStream archiveStream = null;
        ArchiveInputStream ais = null;
        ArchiveOutputStream aos = null;
        List<FileInputStream> streamsToClose = new ArrayList<FileInputStream>();

        File tmpArchive = FileUtils.createEmptyFileWithSamePermissions(pArchive.getFile());

        try {
            aos = factory.createArchiveOutputStream(pArchive.getType(), new FileOutputStream(tmpArchive));
            ChangeSet changes = new ChangeSet();

            if ("jar".equalsIgnoreCase(pArchive.getType())) {
                manifest = manifestWorkaround(pArchive, aos, changes, streamsToClose);
            }

            for (PFile pFile : replaceFiles) {
                String filePath = pFile.getRelativePath();
                File replaceWithFile = pFile.getFile();

                ArchiveEntry archiveEntry = aos.createArchiveEntry(replaceWithFile, filePath);
                FileInputStream fis = new FileInputStream(replaceWithFile);
                streamsToClose.add(fis);
                changes.add(archiveEntry, fis, true);
            }

            archiveStream = new FileInputStream(pArchive.getFile());
            ais = factory.createArchiveInputStream(pArchive.getType(), archiveStream);

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

        if (!pArchive.getFile().delete()) {
            throw new RuntimeException("Couldn't delete file [" + pArchive.getFile().getPath() + "]... Aborting!");
        }
        if (!tmpArchive.renameTo(pArchive.getFile())) {
            throw new RuntimeException(
                    "Couldn't rename filtered file from [" + tmpArchive.getPath() + "] to [" + pArchive.getFile().getPath() + "]... Aborting!");
        }
    }

    private static void replacePArchivesInArchive(PArchive parentArchive, List<PArchive> replaceFiles) {
        ArchiveStreamFactory factory = new ArchiveStreamFactory();

        File manifest = null;
        InputStream archiveStream = null;
        ArchiveInputStream ais = null;
        ArchiveOutputStream aos = null;
        List<FileInputStream> streamsToClose = new ArrayList<FileInputStream>();

        File tmpArchive = FileUtils.createEmptyFileWithSamePermissions(parentArchive.getFile());

        try {
            aos = factory.createArchiveOutputStream(parentArchive.getType(), new FileOutputStream(tmpArchive));
            ChangeSet changes = new ChangeSet();

            if ("jar".equalsIgnoreCase(parentArchive.getType())) {
                manifest = manifestWorkaround(parentArchive, aos, changes, streamsToClose);
            }

            for (PArchive pArchive : replaceFiles) {
                String filePath = pArchive.getRelativePath();
                File replaceWithFile = pArchive.getFile();

                ArchiveEntry archiveEntry = aos.createArchiveEntry(replaceWithFile, filePath);
                FileInputStream fis = new FileInputStream(replaceWithFile);
                streamsToClose.add(fis);
                changes.add(archiveEntry, fis, true);
            }

            archiveStream = new FileInputStream(parentArchive.getFile());
            ais = factory.createArchiveInputStream(parentArchive.getType(), archiveStream);

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

        if (!parentArchive.getFile().delete()) {
            throw new RuntimeException("Couldn't delete file [" + parentArchive.getFile().getPath() + "]... Aborting!");
        }
        if (!tmpArchive.renameTo(parentArchive.getFile())) {
            throw new RuntimeException(
                    "Couldn't rename filtered file from [" + tmpArchive.getPath() + "] to [" + parentArchive.getFile().getPath() + "]... Aborting!");
        }
    }

    // if the archive contains a manifest, we have to simulate a change, otherwise
    // the manifest isn't the first entry anymore.
    private static File manifestWorkaround(PArchive pArchive, ArchiveOutputStream aos, ChangeSet changes, List<FileInputStream> streamsToClose)
            throws IOException {
        String manifestPath = "META-INF/MANIFEST.MF";
        if (!archiveContainsFile(pArchive, manifestPath)) {
            return null;
        }

        PFile manifestPFile = new PFile();
        manifestPFile.setPArchive(pArchive);
        manifestPFile.setRelativePath(manifestPath);
        manifestPFile.setUseRegExResolution(false);

        PFile originalManifestPFile = extractPFile(manifestPFile).get(0);

        ArchiveEntry archiveEntry = aos.createArchiveEntry(originalManifestPFile.getFile(), manifestPath);
        FileInputStream fis = new FileInputStream(originalManifestPFile.getFile());
        streamsToClose.add(fis);
        changes.add(archiveEntry, fis, true);

        return originalManifestPFile.getFile();
    }

    private static Boolean matches(String pathName, String regExp) {
        Path path = FileSystems.getDefault().getPath(pathName);
        PathMatcher matcher = FileSystems.getDefault().getPathMatcher("regex:" + regExp);
        return matcher.matches(path);
    }
}
