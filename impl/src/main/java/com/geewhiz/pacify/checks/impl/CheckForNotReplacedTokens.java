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

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.geewhiz.pacify.checks.PMarkerCheck;
import com.geewhiz.pacify.defect.Defect;
import com.geewhiz.pacify.defect.NotReplacedPropertyDefect;
import com.geewhiz.pacify.managers.EntityManager;
import com.geewhiz.pacify.model.PFile;
import com.geewhiz.pacify.model.PMarker;
import com.geewhiz.pacify.utils.FileUtils;
import com.geewhiz.pacify.utils.RegExpUtils;



public class CheckForNotReplacedTokens implements PMarkerCheck {

    @Override
    public LinkedHashSet<Defect> checkForErrors(EntityManager entityManager, PMarker pMarker) {
        LinkedHashSet<Defect> defects = new LinkedHashSet<Defect>();

        for (PFile pFile : entityManager.getPFilesFrom(pMarker)) {
            String fileContent = FileUtils.getFileInOneString(pFile.getFile(), pFile.getEncoding());
            checkContent(defects, pFile, fileContent);
        }

        return defects;
    }

    private void checkContent(LinkedHashSet<Defect> defects, PFile pFile, String fileContent) {
        for (String property : getNotReplacedProperties(fileContent, pFile.getBeginToken(), pFile.getEndToken())) {
            Defect defect = new NotReplacedPropertyDefect(pFile, property);
            defects.add(defect);
        }
    }

    private List<String> getNotReplacedProperties(String fileContent, String beginToken, String endToken) {
        List<String> result = new ArrayList<String>();

        Pattern pattern = RegExpUtils.getDefaultPattern(beginToken, endToken);
        Matcher matcher = pattern.matcher(fileContent);

        while (matcher.find()) {
            String propertyId = matcher.group(1);
            result.add(propertyId);
        }

        return result;
    }
}
