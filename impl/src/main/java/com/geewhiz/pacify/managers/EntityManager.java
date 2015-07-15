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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.geewhiz.pacify.defect.Defect;
import com.geewhiz.pacify.defect.XMLValidationDefect;
import com.geewhiz.pacify.model.ObjectFactory;
import com.geewhiz.pacify.model.PMarker;
import com.geewhiz.pacify.model.utils.PacifyFilesFinder;

public class EntityManager {

    private Logger        logger = LogManager.getLogger(EntityManager.class.getName());

    private File          startPath;
    private List<PMarker> pMarkers;

    private JAXBContext   jaxbContext;
    private Schema        schema;

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

    public List<Defect> initialize() {
        pMarkers = new ArrayList<PMarker>();
        List<Defect> defects = new ArrayList<Defect>();
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
        return defects;
    }

    public List<PMarker> getPMarkers() {
        if (pMarkers == null) {
            throw new RuntimeException("You didn't initialize the EntityManager. Call initialize().");
        }
        return pMarkers;
    }

    private PMarker unmarshal(File file) throws JAXBException {
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        jaxbUnmarshaller.setSchema(schema);

        return (PMarker) jaxbUnmarshaller.unmarshal(file);
    }
}
