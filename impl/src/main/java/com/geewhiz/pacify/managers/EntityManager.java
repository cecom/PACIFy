/*-
 * ========================LICENSE_START=================================
 * com.geewhiz.pacify.impl
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

package com.geewhiz.pacify.managers;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.geewhiz.pacify.defect.Defect;
import com.geewhiz.pacify.defect.XMLValidationDefect;
import com.geewhiz.pacify.model.ObjectFactory;
import com.geewhiz.pacify.model.PArchive;
import com.geewhiz.pacify.model.PFile;
import com.geewhiz.pacify.model.PMarker;
import com.geewhiz.pacify.model.PProperty;
import com.geewhiz.pacify.model.utils.PArchiveResolver;
import com.geewhiz.pacify.model.utils.PFileResolver;
import com.geewhiz.pacify.model.utils.PacifyFilesFinder;
import com.geewhiz.pacify.postprocessor.DefaultPMarkerPostProcessor;
import com.geewhiz.pacify.postprocessor.PostProcessor;

public class EntityManager {

    private Logger        logger        = LogManager.getLogger(EntityManager.class.getName());

    private File          startPath;
    private List<PMarker> pMarkers;

    private JAXBContext   jaxbContext;
    private Schema        schema;

    private PostProcessor postProcessor = new DefaultPMarkerPostProcessor(this);

    private boolean       initialized    = false;

    public EntityManager(File startPath) {
        this.startPath = startPath;

        try {
            jaxbContext = JAXBContext.newInstance(ObjectFactory.class);

            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            schema = factory.newSchema(new StreamSource(EntityManager.class.getClassLoader().getResourceAsStream("pacify.xsd")));
        } catch (Exception e) {
            throw new RuntimeException("Couldn't instanciate jaxb.", e);
        }
    }

    public int getPMarkerCount() {
        return (new PacifyFilesFinder(startPath).getPacifyFiles()).size();
    }

    public LinkedHashSet<Defect> initialize() {
        LinkedHashSet<Defect> defects = new LinkedHashSet<Defect>();
        if (initialized) {
            return defects;
        }

        pMarkers = new ArrayList<PMarker>();
        for (File markerFile : new PacifyFilesFinder(startPath).getPacifyFiles()) {
            try {
                PMarker pMarker = unmarshal(markerFile);
                pMarker.setFile(markerFile);

                pMarkers.add(pMarker);
            } catch (JAXBException e) {
                defects.add(new XMLValidationDefect(markerFile));
                logger.debug("Error while parsing file [" + markerFile.getAbsolutePath() + "]", e);
            }
        }

        initialized = true;

        return defects;
    }

    public List<PMarker> getPMarkers() {
        if (pMarkers == null) {
            throw new RuntimeException("You didn't initialize the EntityManager. Call initialize().");
        }
        return pMarkers;
    }

    public List<PFile> getPFilesFrom(PMarker pMarker) {
        if (pMarker.isResolved()) {
            return pMarker.getPFiles();
        }

        List<PFile> result = new ArrayList<PFile>();

        for (Object entry : pMarker.getFilesAndArchives()) {
            if (entry instanceof PFile) {
                PFile pFile = (PFile) entry;
                PFileResolver resolver = new PFileResolver(pFile);
                result.addAll(resolver.resolve());
            } else if (entry instanceof PArchive) {
                PArchive pArchive = (PArchive) entry;
                PArchiveResolver resolver = new PArchiveResolver(pArchive);
                result.addAll(resolver.resolve());
            } else {
                throw new NotImplementedException("Type not implemented [" + entry.getClass() + "]");
            }
        }

        pMarker.getFilesAndArchives().clear();
        pMarker.getFilesAndArchives().addAll(result);
        pMarker.setResolved(Boolean.TRUE);

        return result;
    }

    public List<PProperty> getPPropertiesFrom(PMarker pMarker) {
        List<PProperty> result = new ArrayList<PProperty>();

        for (PFile pFile : getPFilesFrom(pMarker)) {
            result.addAll(pFile.getPProperties());
        }

        return result;
    }

    private PMarker unmarshal(File file) throws JAXBException {
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        jaxbUnmarshaller.setSchema(schema);

        return (PMarker) jaxbUnmarshaller.unmarshal(file);
    }

    public void postProcessPMarker(PMarker pMarker, LinkedHashSet<Defect> pMarkerDefects) {
        getPostProcessor().doPostProcess(pMarker, pMarkerDefects);
    }

    public PostProcessor getPostProcessor() {
        return postProcessor;
    }

    public void setPostProcessor(PostProcessor postProcessor) {
        this.postProcessor = postProcessor;
    }

}
