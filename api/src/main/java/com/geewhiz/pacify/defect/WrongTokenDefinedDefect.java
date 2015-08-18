package com.geewhiz.pacify.defect;

import com.geewhiz.pacify.model.PArchive;
import com.geewhiz.pacify.model.PFile;
import com.geewhiz.pacify.model.PMarker;

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

public class WrongTokenDefinedDefect implements Defect {

    // TODO: Not used anymore
    private PMarker  pMarker;
    private PArchive pArchive;
    private PFile    pFile;
    private String   errorMessage;

    public WrongTokenDefinedDefect(PMarker pMarker, PFile pFile, String errorMessage) {

        this.pMarker = pMarker;
        this.pFile = pFile;
        this.errorMessage = errorMessage;
    }

    public WrongTokenDefinedDefect(PMarker pMarker, PArchive pArchive, PFile pFile, String errorMessage) {
        this(pMarker, pFile, errorMessage);
        this.pArchive = pArchive;
    }

    public String getDefectMessage() {
        StringBuffer result = new StringBuffer();
        result.append(String.format("WrongTokenDefined: \n\t[MarkerFile=%s]", pMarker.getFile().getAbsolutePath()));
        if (pArchive != null) {
            result.append(String.format("\n\t[Archive=%s]", pMarker.getAbsoluteFileFor(pArchive)));
            if (pFile != null) {
                result.append(String.format("\n\t[Archive File=%s]", pFile.getRelativePath()));
            }
        } else {
            result.append(String.format("\n\t[File=%s]", pMarker.getAbsoluteFileFor(pFile)));
        }

        result.append(String.format("\n\t[ErrorMessage=%s]", errorMessage));
        return result.toString();
    }
}
