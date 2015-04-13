package com.geewhiz.pacify.model.utils;

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
import java.util.Arrays;
import java.util.List;

public class PacifyFilesFinder {
    private static CMFileFileFilter pfListFilenameFilter = new CMFileFileFilter();
    private static DirFilter dirFilter = new DirFilter();

    private File folderToCheck;

    public PacifyFilesFinder(File folderToCheck) {
        this.folderToCheck = folderToCheck;
    }

    public List<File> getPacifyFiles() {
        List<File> pfListFiles = new ArrayList<File>();
        addPFListFiles(pfListFiles, folderToCheck);
        return pfListFiles;
    }

    private void addPFListFiles(List<File> pfListFiles, File folderToCheck) {
        if (folderToCheck == null) {
            throw new IllegalArgumentException("Folder is null.... Aborting!");
        }
        if (!folderToCheck.exists()) {
            throw new IllegalArgumentException("Folder [" + folderToCheck.getAbsolutePath()
                    + "] does not exist... Aborting!");
        }

        pfListFiles.addAll(Arrays.asList(folderToCheck.listFiles(pfListFilenameFilter)));

        File[] subFolders = folderToCheck.listFiles(dirFilter);
        for (File subFolder : subFolders) {
            addPFListFiles(pfListFiles, subFolder);
        }
    }
}
