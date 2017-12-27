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

package com.geewhiz.pacify.model.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.NotImplementedException;

import com.geewhiz.pacify.model.PArchive;
import com.geewhiz.pacify.model.PFile;
import com.geewhiz.pacify.utils.ArchiveUtils;

public class PArchiveResolver {

    PArchive pArchive;

    public PArchiveResolver(PArchive pArchive) {
        this.pArchive = pArchive;
    }

    public List<PFile> resolve() {
        List<PFile> result = new ArrayList<PFile>();

        if (!ArchiveUtils.isArchiveAndIsSupported(pArchive.getRelativePath())) {
            // this is checked via an validator.
            return result;
        }

        pArchive.setType(ArchiveUtils.getArchiveType(pArchive));

        if (pArchive.getFile() == null) {
            File physicalFile = null;
            if (pArchive.isArchiveFile()) {
                physicalFile = ArchiveUtils.extractPArchive(pArchive);
            } else {
                physicalFile = new File(pArchive.getPMarker().getFolder(), pArchive.getRelativePath());
            }
            pArchive.setFile(physicalFile);
        }

        for (Object entry : pArchive.getFilesAndArchives()) {
            if (entry instanceof PFile) {
                PFile pFile = (PFile) entry;
                result.addAll(ArchiveUtils.extractPFile(pFile));
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

}
