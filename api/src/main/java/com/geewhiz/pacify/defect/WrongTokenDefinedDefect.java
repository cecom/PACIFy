package com.geewhiz.pacify.defect;

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

    private PMarker pMarker;
    private PFile   pFile;
    private String  errorMessage;

    public WrongTokenDefinedDefect(PMarker pMarker, PFile pFile, String errorMessage) {
        this.pMarker = pMarker;
        this.pFile = pFile;
        this.errorMessage = errorMessage;
    }

    public String getDefectMessage() {
        return String.format("WrongTokenDefined: \n\t[MarkerFile=%s]\n\t[File=%s]\n\t[FilterClass=%s]\n\t[ErrorMessage=%s]",
                pMarker.getFile().getAbsolutePath(),
                pFile.getRelativePath(), pFile.getFilterClass(), errorMessage);
    }
}
