package com.geewhiz.pacify.managers;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
import org.apache.commons.lang.NotImplementedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.geewhiz.pacify.checks.impl.CheckForNotReplacedTokens;
import com.geewhiz.pacify.defect.ArchiveTypeNotImplementedDefect;
import com.geewhiz.pacify.defect.Defect;
import com.geewhiz.pacify.defect.FileDoesNotExistDefect;
import com.geewhiz.pacify.defect.FilterNotFoundDefect;
import com.geewhiz.pacify.filter.PacifyFilter;
import com.geewhiz.pacify.model.PArchive;
import com.geewhiz.pacify.model.PFile;
import com.geewhiz.pacify.model.PMarker;
import com.geewhiz.pacify.model.PProperty;
import com.geewhiz.pacify.utils.FileUtils;

public class FilterManager {

    private Logger                 logger = LogManager.getLogger(FilterManager.class.getName());

    private PropertyResolveManager propertyResolveManager;
    private PMarker                pMarker;

    public FilterManager(PropertyResolveManager propertyResolveManager, PMarker pMarker) {
        this.propertyResolveManager = propertyResolveManager;
        this.pMarker = pMarker;
    }

    public List<Defect> doFilter() {
        List<Defect> defects = new ArrayList<Defect>();

        for (Object entry : pMarker.getFilesAndArchives()) {
            if (entry instanceof PFile) {
                defects.addAll(filterPFile((PFile) entry));
            } else if (entry instanceof PArchive) {
                defects.addAll(filterPArchive((PArchive) entry));
            } else {
                throw new NotImplementedException("Filter implementation for " + entry.getClass().getName() + " not implemented.");
            }
        }

        if (defects.isEmpty()) {
            pMarker.getFile().delete();
        }
        return defects;
    }

    private List<? extends Defect> filterPFile(PFile pFile) {
        logger.debug("     Filtering [{}] using encoding [{}] and filter [{}]", pMarker.getAbsoluteFileFor(pFile).getAbsolutePath(), pFile.getEncoding(),
                pFile.getFilterClass());

        File file = pMarker.getAbsoluteFileFor(pFile);
        if (!file.exists()) {
            return Arrays.asList(new FileDoesNotExistDefect(pMarker, pFile));
        }

        PacifyFilter pacifyFilter = getFilterForPFile(pFile);

        if (pacifyFilter == null) {
            return Arrays.asList(new FilterNotFoundDefect(pMarker, pFile));
        }

        Map<String, String> propertyValues = getPropertyValues(pFile);
        String beginToken = pMarker.getBeginTokenFor(pFile);
        String endToken = pMarker.getEndTokenFor(pFile);
        String encoding = pFile.getEncoding();

        List<Defect> defects = new ArrayList<Defect>();
        defects.addAll(pacifyFilter.filter(propertyValues, beginToken, endToken, file, encoding));

        CheckForNotReplacedTokens checker = new CheckForNotReplacedTokens();
        defects.addAll(checker.checkForErrors(pMarker, pFile));

        return defects;
    }

    private List<? extends Defect> filterPArchive(PArchive pArchive) {
        File file = pMarker.getAbsoluteFileFor(pArchive);
        if (!file.exists()) {
            return Arrays.asList(new FileDoesNotExistDefect(pMarker, pArchive));
        }

        String archiveType = getArchiveType(pArchive);
        if (archiveType == null) {
            return Arrays.asList(new ArchiveTypeNotImplementedDefect(pMarker, pArchive));
        }

        List<Defect> defects = new ArrayList<Defect>();

        for (PFile pFile : pArchive.getPFiles()) {
            File extractedFile = extractFile(pArchive, pFile);

            PacifyFilter pacifyFilter = getFilterForPFile(pFile);

            if (pacifyFilter == null) {
                defects.add(new FilterNotFoundDefect(pMarker, pArchive, pFile));
                continue;
            }

            Map<String, String> propertyValues = getPropertyValues(pFile);
            String beginToken = pMarker.getBeginTokenFor(pArchive, pFile);
            String endToken = pMarker.getEndTokenFor(pArchive, pFile);
            String encoding = pFile.getEncoding();

            defects.addAll(pacifyFilter.filter(propertyValues, beginToken, endToken, extractedFile, encoding));

            replaceFileInArchive(pArchive, pFile, extractedFile);
        }
        return defects;
    }

    private File extractFile(PArchive pArchive, PFile pFile) {
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
                result.deleteOnExit();

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
            return null;
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

    private void replaceFileInArchive(PArchive pArchive, PFile pFile, File replaceWith) {
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

    private String getArchiveType(PArchive pArchive) {
        if ("jar".equalsIgnoreCase(pArchive.getType())) {
            return ArchiveStreamFactory.JAR;
        }
        if ("war".equalsIgnoreCase(pArchive.getType())) {
            return ArchiveStreamFactory.JAR;
        }
        if ("ear".equalsIgnoreCase(pArchive.getType())) {
            return ArchiveStreamFactory.JAR;
        }
        if ("zip".equalsIgnoreCase(pArchive.getType())) {
            return ArchiveStreamFactory.ZIP;
        }
        if ("tar".equalsIgnoreCase(pArchive.getType())) {
            return ArchiveStreamFactory.TAR;
        }
        return null;
    }

    private Map<String, String> getPropertyValues(PFile pFile) {
        HashMap<String, String> result = new HashMap<String, String>();

        for (PProperty pProperty : pFile.getPProperties()) {
            String propertyName = pProperty.getName();
            String propertyValue = propertyResolveManager.getPropertyValue(pProperty);
            result.put(propertyName, propertyValue);
        }

        return result;
    }

    private PacifyFilter getFilterForPFile(PFile pFile) {
        return getFilterForPFile(null, pFile);
    }

    private PacifyFilter getFilterForPFile(PArchive pArchive, PFile pFile) {
        String filterClass = pFile.getFilterClass();

        try {
            return (PacifyFilter) Class.forName(filterClass).getConstructor(PMarker.class, PArchive.class, PFile.class).newInstance(pMarker, pArchive, pFile);
        } catch (Exception e) {
            logger.debug("Error while instantiate filter class [" + filterClass + "]", e);
            return null;
        }
    }
}
