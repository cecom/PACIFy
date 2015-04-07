package com.geewhiz.pacify.model;

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

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Transient;

@Root(name = "property_file_list", strict = false)
public class PFListEntity {

    @ElementList(name = "property", inline = true)
    private List<PFPropertyEntity> pfPropertyEntities;

    @Transient
    private File file;

    public List<PFPropertyEntity> getPfPropertyEntities() {
        return pfPropertyEntities;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    public File getFolder() {
        return file.getParentFile();
    }

    public File getAbsoluteFileFor(PFFileEntity pfFileEntity) {
        return new File(getFolder(), pfFileEntity.getRelativePath());
    }

    public List<PFFileEntity> getPfFileEntities() {
        List<PFFileEntity> result = new ArrayList<PFFileEntity>();
        for (PFPropertyEntity pfPropertyEntity : getPfPropertyEntities()) {
            for (PFFileEntity pfFileEntity : pfPropertyEntity.getPFFileEntities()) {
                if (result.contains(pfFileEntity)) {
                    continue;
                }
                result.add(pfFileEntity);
            }
        }
        return result;
    }

    public List<PFPropertyEntity> getPfPropertyEntitiesForPFFileEntity(PFFileEntity pfFileEntity) {
        List<PFPropertyEntity> result = new ArrayList<PFPropertyEntity>();
        for (PFPropertyEntity pfPropertyEntity : getPfPropertyEntities()) {
            if (pfPropertyEntity.getPFFileEntities().contains(pfFileEntity)) {
                result.add(pfPropertyEntity);
            }
        }
        return result;
    }
}
