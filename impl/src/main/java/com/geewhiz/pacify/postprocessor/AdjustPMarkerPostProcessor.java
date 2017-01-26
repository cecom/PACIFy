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
package com.geewhiz.pacify.postprocessor;

import java.util.LinkedHashSet;

import com.geewhiz.pacify.defect.Defect;
import com.geewhiz.pacify.defect.DefectException;
import com.geewhiz.pacify.managers.EntityManager;
import com.geewhiz.pacify.model.PMarker;
import com.geewhiz.pacify.model.PProperty;
import com.geewhiz.pacify.model.utils.XMLUtils;
import com.geewhiz.pacify.utils.ArchiveUtils;

public class AdjustPMarkerPostProcessor implements PostProcessor {

    private EntityManager entityManager;

    public AdjustPMarkerPostProcessor(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void doPostProcess(PMarker pMarker, LinkedHashSet<Defect> defects) {
        if (defects.isEmpty()) {
            pMarker.getFile().delete();
        } else {
            markDefectedPProperties(pMarker, defects);
            adjustPMarkerFile(pMarker);
        }

        ArchiveUtils.replaceFilesInArchives(entityManager.getPFilesFrom(pMarker));
    }

    private void markDefectedPProperties(PMarker pMarker, LinkedHashSet<Defect> defects) {
        // we can't do whitelisting, so first mark everything as successfully
        for (PProperty pProperty : entityManager.getPPropertiesFrom(pMarker)) {
            pProperty.setSuccessfullyProcessed(Boolean.TRUE);
        }

        // now mark all properties which have a defect
        for (Defect defect : defects) {
            if (!(defect instanceof DefectException)) {
                continue;
            }
            DefectException defectException = (DefectException) defect;

            PProperty pProperty = defectException.getPProperty();
            if (pProperty != null) {
                pProperty.setSuccessfullyProcessed(Boolean.FALSE);
                continue;
            }
        }
    }

    private void adjustPMarkerFile(PMarker pMarker) {
        XMLUtils xmlUtils = new XMLUtils(pMarker);

        for (PProperty pProperty : entityManager.getPPropertiesFrom(pMarker)) {
            if (pProperty.isSuccessfullyProcessed()) {
                removeEntry(xmlUtils, pProperty);
            } else {
                xmlUtils.addIfItDoesNotExist(pProperty);
            }

            addAllReferences(xmlUtils, pProperty);
        }

        xmlUtils.removeEntriesWithoutChilds();
        xmlUtils.writeDocument();
    }

    private void removeEntry(XMLUtils xmlUtils, PProperty pProperty) {
        if (pProperty.getPFile().isClone()) {
            // we could resolve the regex, so delete the complete entry.
            xmlUtils.deleteIfExist(pProperty.getPFile().isCloneFrom().getXPath());
        } else {
            xmlUtils.deleteIfExist(pProperty.getXPath());
        }
    }

    private void addAllReferences(XMLUtils xmlUtils, PProperty pProperty) {
        for (PProperty reference : pProperty.getReferencedProperties()) {
            xmlUtils.addIfItDoesNotExist(reference);
            addAllReferences(xmlUtils, reference);
        }
    }

}
