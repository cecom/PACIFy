package com.geewhiz.pacify.defect;

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

import com.geewhiz.pacify.model.PArchive;
import com.geewhiz.pacify.model.PFile;
import com.geewhiz.pacify.model.PMarker;

public class FileDoesNotExistDefect implements Defect {

    private PMarker  pMarker;
    private PFile    pFile;
    private PArchive pArchive;

    public FileDoesNotExistDefect(PMarker pMarker, PFile pFile) {
        this.pMarker = pMarker;
        this.pFile = pFile;
    }

    public FileDoesNotExistDefect(PMarker pMarker, PArchive pArchive) {
        this.pMarker = pMarker;
        this.pArchive = pArchive;
    }

    public String getDefectMessage() {
        StringBuffer message = new StringBuffer();
        message.append(String.format("TargetFileDoesNotExist: \n\t[MarkerFile=%s]", pMarker.getFile().getAbsolutePath()));
        if (pArchive != null) {
            message.append(String.format("\n\t[Archive=%s]", pMarker.getAbsoluteFileFor(pArchive).getAbsolutePath()));
            if (pFile != null) {
                message.append(String.format("\n\t[Archive File=%s]", pFile.getRelativePath()));
            }
        } else {
            message.append(String.format("\n\t[File=%s]", pMarker.getAbsoluteFileFor(pFile).getAbsolutePath()));
        }

        return message.toString();
    }
}
