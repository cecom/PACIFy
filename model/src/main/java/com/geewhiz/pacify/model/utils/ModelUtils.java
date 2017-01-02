/*-
 * ========================LICENSE_START=================================
 * com.geewhiz.pacify.model
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

import com.geewhiz.pacify.model.PFile;

public class ModelUtils {

    public static PFile createPFile(PFile cloneFrom, String relativePath, File physicalPath) {
        PFile aClone = (PFile) cloneFrom.clone();

        aClone.setFile(physicalPath);
        aClone.setUseRegExResolution(false);
        aClone.setRelativePath(relativePath);

        return aClone;
    }
}
