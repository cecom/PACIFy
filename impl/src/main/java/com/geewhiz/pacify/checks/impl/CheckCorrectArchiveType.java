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

package com.geewhiz.pacify.checks.impl;



import java.util.LinkedHashSet;

import com.geewhiz.pacify.checks.PMarkerCheck;
import com.geewhiz.pacify.defect.ArchiveTypeNotImplementedDefect;
import com.geewhiz.pacify.defect.Defect;
import com.geewhiz.pacify.managers.EntityManager;
import com.geewhiz.pacify.model.PArchive;
import com.geewhiz.pacify.model.PMarker;
import com.geewhiz.pacify.utils.ArchiveUtils;

public class CheckCorrectArchiveType implements PMarkerCheck {

    public LinkedHashSet<Defect> checkForErrors(EntityManager entityManager, PMarker pMarker) {
        LinkedHashSet<Defect> defects = new LinkedHashSet<Defect>();

        for (Object entry : pMarker.getFilesAndArchives()) {
            if (entry instanceof PArchive) {
                PArchive pArchive = (PArchive) entry;

                if (!ArchiveUtils.isArchiveAndIsSupported(pArchive.getRelativePath())) {
                    defects.add(new ArchiveTypeNotImplementedDefect(pArchive));
                }
            }
        }

        return defects;
    }
}
