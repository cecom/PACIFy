package com.geewhiz.pacify.checks.impl;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.geewhiz.pacify.checks.PMarkerCheck;
import com.geewhiz.pacify.defect.Defect;
import com.geewhiz.pacify.defect.NoPlaceholderInTargetFileDefect;
import com.geewhiz.pacify.managers.MarkerFileManager;
import com.geewhiz.pacify.model.PFile;
import com.geewhiz.pacify.model.PMarker;
import com.geewhiz.pacify.model.PProperty;
import com.geewhiz.pacify.utils.FileUtils;

public class CheckPlaceholderExistsInTargetFile implements PMarkerCheck {

    public List<Defect> checkForErrors(PMarker pMarker) {
        List<Defect> defects = new ArrayList<Defect>();

        for (PProperty pproperty : pMarker.getProperties()) {
            for (PFile pfile : pproperty.getFiles()) {
                File file = pMarker.getAbsoluteFileFor(pfile);
                
                if(!file.exists()){
                	//is checked before, so don'‚t throw any exception.
                	continue;
                }
                
                boolean exists = doesPropertyExistInFile(pproperty, file, pfile.getEncoding());
                if (exists) {
                    continue;
                }
                Defect defect = new NoPlaceholderInTargetFileDefect(pMarker, pproperty, pfile);
                defects.add(defect);
            }
        }

        return defects;
    }

    public boolean doesPropertyExistInFile(PProperty pproperty, File file, String encoding) {
        String fileContent = FileUtils.getFileInOneString(file, encoding);

        // todo: das pattern muss raus, kann file spezifisch sein
        Pattern pattern = MarkerFileManager.getPattern(pproperty.getName(), true);
        Matcher matcher = pattern.matcher(fileContent);

        return matcher.find();
    }
}