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
import java.util.List;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.changes.ChangeSet;
import org.apache.commons.compress.changes.ChangeSetPerformer;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;

import com.geewhiz.pacify.defect.DefectException;
import com.geewhiz.pacify.defect.FileDoesNotExistDefect;
import com.geewhiz.pacify.model.PArchive;
import com.geewhiz.pacify.model.PFile;
import com.geewhiz.pacify.model.PMarker;

public class FileUtils {

    public static String getFileInOneString(File file, String encoding) {
        InputStream is = null;
        try {
            is = new FileInputStream(file);
            return IOUtils.toString(is, Charsets.toCharset(encoding));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        finally {
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
        }
        finally {
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

    public static File createTempFile(File folder, String prefix) {
        try {
            return File.createTempFile(prefix, "tmp", folder);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Boolean archiveContainsFile(PMarker pMarker, PArchive pArchive, PFile pFile) {
        ArchiveInputStream ais = null;
        try {
            ArchiveStreamFactory factory = new ArchiveStreamFactory();

            File archiveFile = pMarker.getAbsoluteFileFor(pArchive);
            ais = factory.createArchiveInputStream(pArchive.getType(), new FileInputStream(archiveFile));

            ArchiveEntry entry;
            while ((entry = ais.getNextEntry()) != null) {
                if (pFile.getRelativePath().equals(entry.getName())) {
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
        }
        finally {
            IOUtils.closeQuietly(ais);
        }
    }

    public static String getFileInOneString(PMarker pMarker, PArchive pArchive, PFile pFile) throws DefectException {
        ArchiveInputStream ais = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            ArchiveStreamFactory factory = new ArchiveStreamFactory();

            File archiveFile = pMarker.getAbsoluteFileFor(pArchive);
            ais = factory.createArchiveInputStream(pArchive.getType(), new FileInputStream(archiveFile));

            ArchiveEntry entry;

            while ((entry = ais.getNextEntry()) != null) {
                if (!pFile.getRelativePath().equals(entry.getName())) {
                    continue;
                }

                byte[] content = new byte[2048];
                BufferedOutputStream bos = new BufferedOutputStream(baos);

                int len;
                while ((len = ais.read(content)) != -1)
                {
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
        }
        finally {
            IOUtils.closeQuietly(baos);
            IOUtils.closeQuietly(ais);
        }
    }

    public static File extractFile(PMarker pMarker, PArchive pArchive, PFile pFile) throws DefectException {
        ArchiveInputStream ais = null;
        try {
            ArchiveStreamFactory factory = new ArchiveStreamFactory();

            File archiveFile = pMarker.getAbsoluteFileFor(pArchive);
            ais = factory.createArchiveInputStream(pArchive.getType(), new FileInputStream(archiveFile));

            ArchiveEntry entry;
            while ((entry = ais.getNextEntry()) != null) {
                if (!pFile.getRelativePath().equals(entry.getName())) {
                    continue;
                }

                File result = FileUtils.createTempFile(archiveFile.getParentFile(), archiveFile.getName());

                byte[] content = new byte[2048];
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(result));

                int len;
                while ((len = ais.read(content)) != -1)
                {
                    bos.write(content, 0, len);
                }
                bos.close();
                content = null;

                return result;
            }

            throw new FileDoesNotExistDefect(pMarker, pArchive, pFile);

        } catch (ArchiveException e) {
            throw new RuntimeException(e);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        finally {
            IOUtils.closeQuietly(ais);
        }
    }

    public static void replaceFileInArchive(PMarker pMarker, PArchive pArchive, PFile pFile, File replaceWith) {
        ArchiveStreamFactory factory = new ArchiveStreamFactory();

        InputStream archiveInputStream = null;
        ArchiveInputStream ais = null;
        ArchiveOutputStream aos = null;

        File archiveFile = pMarker.getAbsoluteFileFor(pArchive);
        File tmpZip = FileUtils.createTempFile(archiveFile.getParentFile(), archiveFile.getName());

        ArchiveEntry entry;
        try {

            String fileToReplace = pFile.getRelativePath();

            aos = factory.createArchiveOutputStream(pArchive.getType(), new FileOutputStream(tmpZip));
            entry = aos.createArchiveEntry(replaceWith, fileToReplace);

            ChangeSet changes = new ChangeSet();
            changes.add(entry, new FileInputStream(replaceWith), true);

            archiveInputStream = new FileInputStream(archiveFile);
            ais = factory.createArchiveInputStream(pArchive.getType(), archiveInputStream);

            ChangeSetPerformer performer = new ChangeSetPerformer(changes);
            performer.perform(ais, aos);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ArchiveException e) {
            throw new RuntimeException(e);
        }
        finally {
            IOUtils.closeQuietly(aos);
            IOUtils.closeQuietly(ais);
            IOUtils.closeQuietly(archiveInputStream);
        }

        if (!archiveFile.delete()) {
            throw new RuntimeException("Couldn't delete file [" + archiveFile.getPath() + "]... Aborting!");
        }
        if (!tmpZip.renameTo(archiveFile)) {
            throw new RuntimeException("Couldn't rename filtered file from [" + tmpZip.getPath() + "] to ["
                    + archiveFile.getPath() + "]... Aborting!");
        }
    }
}
