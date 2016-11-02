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
import java.io.ByteArrayOutputStream;
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
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.geewhiz.pacify.defect.ArchiveDefect;
import com.geewhiz.pacify.defect.DefectException;
import com.geewhiz.pacify.defect.FileDoesNotExistDefect;
import com.geewhiz.pacify.model.PArchive;
import com.geewhiz.pacify.model.PFile;
import com.geewhiz.pacify.model.PMarker;

public class FileUtils {

    private static Logger logger = LogManager.getLogger(FileUtils.class.getName());

    private static final boolean IS_POSIX = FileSystems.getDefault().supportedFileAttributeViews().contains("posix");

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
        try {
            File folder = forFile.getParentFile();
            String prefix = forFile.getName();

            File tmp = File.createTempFile(prefix, "tmp", folder);

            if (IS_POSIX) {
                Set<PosixFilePermission> attrs = Files.getPosixFilePermissions(Paths.get(forFile.toURI()));
                Files.setPosixFilePermissions(Paths.get(tmp.toURI()), attrs);
            }

            return tmp;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Boolean archiveContainsFile(PMarker pMarker, PArchive pArchive, PFile pFile) {
        return archiveContainsFile(pMarker, pArchive, pFile.getRelativePath());
    }

    public static Boolean archiveContainsFile(PMarker pMarker, PArchive pArchive, String file) {
        ArchiveInputStream ais = null;
        try {
            ArchiveStreamFactory factory = new ArchiveStreamFactory();

            File archiveFile = pArchive.getFile();
            ais = factory.createArchiveInputStream(pArchive.getType(), new FileInputStream(archiveFile));

            ArchiveEntry entry;
            while ((entry = ais.getNextEntry()) != null) {
                if (file.equals(entry.getName())) {
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

    public static String getFileInOneString(PMarker pMarker, PArchive pArchive, PFile pFile) throws DefectException {
        ArchiveInputStream ais = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            ArchiveStreamFactory factory = new ArchiveStreamFactory();

            File archiveFile = pArchive.getFile();
            ais = factory.createArchiveInputStream(pArchive.getType(), new FileInputStream(archiveFile));

            ArchiveEntry entry;

            while ((entry = ais.getNextEntry()) != null) {
                if (!pFile.getRelativePath().equals(entry.getName())) {
                    continue;
                }

                byte[] content = new byte[2048];
                BufferedOutputStream bos = new BufferedOutputStream(baos);

                int len;
                while ((len = ais.read(content)) != -1) {
                    bos.write(content, 0, len);
                }
                bos.close();
                content = null;

                return baos.toString(pFile.getEncoding());
            }

            throw new FileDoesNotExistDefect(pMarker, pArchive, pFile);
        } catch (ArchiveException e) {
            throw new RuntimeException(e);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(baos);
            IOUtils.closeQuietly(ais);
        }
    }

    public static File extractFile(PMarker pMarker, PArchive pArchive, PFile pFile) throws DefectException {
        try {
            return extractFile(pMarker, pArchive, pFile.getRelativePath());
        } catch (FileNotFoundException e) {
            throw new FileDoesNotExistDefect(pMarker, pArchive, pFile);
        }
    }

    public static File extractFile(PMarker pMarker, PArchive pArchive, String file) throws FileNotFoundException {
        File result = null;
        ArchiveInputStream ais = null;
        try {
            ArchiveStreamFactory factory = new ArchiveStreamFactory();

            File archiveFile = pArchive.getFile();
            ais = factory.createArchiveInputStream(pArchive.getType(), new FileInputStream(archiveFile));

            ArchiveEntry entry;
            while ((entry = ais.getNextEntry()) != null) {
                if (!file.equals(entry.getName())) {
                    continue;
                }

                result = FileUtils.createEmptyFileWithSamePermissions(archiveFile);

                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(result));

                byte[] content = new byte[2048];

                int len;
                while ((len = ais.read(content)) != -1) {
                    bos.write(content, 0, len);
                }

                bos.close();
                content = null;

                return result;
            }
        } catch (ArchiveException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(ais);
        }

        throw new FileNotFoundException("Couldn't find file [" + file + "]");
    }

    public static void replaceFilesInArchive(PMarker pMarker, PArchive pArchive, Map<PFile, File> replaceFiles) throws ArchiveDefect {
        ArchiveStreamFactory factory = new ArchiveStreamFactory();

        File manifest = null;
        InputStream archiveStream = null;
        ArchiveInputStream ais = null;
        ArchiveOutputStream aos = null;
        List<FileInputStream> streamsToClose = new ArrayList<FileInputStream>();

        File archiveFile = pArchive.getFile();
        File tmpArchive = FileUtils.createEmptyFileWithSamePermissions(archiveFile);

        try {
            aos = factory.createArchiveOutputStream(pArchive.getType(), new FileOutputStream(tmpArchive));
            ChangeSet changes = new ChangeSet();

            manifest = manifestWorkaround(pMarker, pArchive, aos, changes, streamsToClose);

            for (Entry<PFile, File> entry : replaceFiles.entrySet()) {
                String filePath = entry.getKey().getRelativePath();
                File replaceWithFile = entry.getValue();

                ArchiveEntry archiveEntry = aos.createArchiveEntry(replaceWithFile, filePath);
                FileInputStream fis = new FileInputStream(replaceWithFile);
                streamsToClose.add(fis);
                changes.add(archiveEntry, fis, true);
            }

            archiveStream = new FileInputStream(archiveFile);
            ais = factory.createArchiveInputStream(pArchive.getType(), archiveStream);

            ChangeSetPerformer performer = new ChangeSetPerformer(changes);
            performer.perform(ais, aos);

        } catch (IOException e) {
            logger.debug(e);
            throw new ArchiveDefect(pMarker, pArchive, "Error while replacing file in archive.");
        } catch (ArchiveException e) {
            logger.debug(e);
            throw new ArchiveDefect(pMarker, pArchive, "Error while replacing file in archive.");
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

        if (!archiveFile.delete()) {
            throw new RuntimeException("Couldn't delete file [" + archiveFile.getPath() + "]... Aborting!");
        }
        if (!tmpArchive.renameTo(archiveFile)) {
            throw new RuntimeException("Couldn't rename filtered file from [" + tmpArchive.getPath() + "] to [" + archiveFile.getPath() + "]... Aborting!");
        }

    }

    // if the archive contains a manifest, we have to simulate a change, otherwise
    // the manifest isn't the first entry anymore.
    private static File manifestWorkaround(PMarker pMarker, PArchive pArchive, ArchiveOutputStream aos, ChangeSet changes, List<FileInputStream> streamsToClose)
            throws IOException {
        String manifestPath = "META-INF/MANIFEST.MF";
        if (!archiveContainsFile(pMarker, pArchive, manifestPath)) {
            return null;
        }

        File originalManifest = extractFile(pMarker, pArchive, manifestPath);

        ArchiveEntry archiveEntry = aos.createArchiveEntry(originalManifest, manifestPath);
        FileInputStream fis = new FileInputStream(originalManifest);
        streamsToClose.add(fis);
        changes.add(archiveEntry, fis, true);

        return originalManifest;
    }
}
