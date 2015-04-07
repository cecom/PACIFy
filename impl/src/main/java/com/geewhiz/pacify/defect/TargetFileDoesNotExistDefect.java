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

import com.geewhiz.pacify.model.PFFileEntity;
import com.geewhiz.pacify.model.PFListEntity;
import com.geewhiz.pacify.model.PFPropertyEntity;

public class TargetFileDoesNotExistDefect implements Defect {

    private PFListEntity pfListEntity;
    private PFPropertyEntity pfPropertyEntity;
    private PFFileEntity pfFileEntity;

    public TargetFileDoesNotExistDefect(PFListEntity pfListEntity, PFPropertyEntity pfPropertyEntity,
        PFFileEntity pfFileEntity) {
        this.pfListEntity = pfListEntity;
        this.pfPropertyEntity = pfPropertyEntity;
        this.pfFileEntity = pfFileEntity;
    }

    public String getDefectMessage() {
        return "File [" + pfListEntity.getAbsoluteFileFor(pfFileEntity).getPath() + "] which is defined in ["
                + pfListEntity.getFile().getPath()
                + "] property [" + pfPropertyEntity.getId() + "] " + "does not exist.";
    }

}
