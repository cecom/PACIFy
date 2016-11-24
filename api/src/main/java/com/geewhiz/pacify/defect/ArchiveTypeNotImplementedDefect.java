package com.geewhiz.pacify.defect;

import com.geewhiz.pacify.defect.DefectException;
import com.geewhiz.pacify.model.PArchive;

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

public class ArchiveTypeNotImplementedDefect extends DefectException {

    private static final long serialVersionUID = 1L;

    public ArchiveTypeNotImplementedDefect(PArchive pArchive) {
        super(pArchive);
    }

    @Override
    public String getDefectMessage() {
        return super.getDefectMessage() + String.format("\n\t[Type=%s]", getPArchive().getInternalType());
    }
}
