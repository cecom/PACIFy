/*-
 * ========================LICENSE_START=================================
 * com.geewhiz.pacify.api
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

package com.geewhiz.pacify.defect;

import com.geewhiz.pacify.defect.DefectException;
import com.geewhiz.pacify.model.PFile;



public class FilterNotFoundDefect extends DefectException {

    private static final long serialVersionUID = 1L;

    public FilterNotFoundDefect(PFile pFile) {
        super(pFile);
    }

    @Override
    public String getDefectMessage() {
        return super.getDefectMessage() + String.format("\n\t[Filter=%s]", getPFile().getFilterClass())
                + String.format("\n\t[Message=%s]", "Couldn't find filter class or couldn't initialize it. Have a look at the debug output.");
    }
}
