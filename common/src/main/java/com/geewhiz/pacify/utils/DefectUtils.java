/*-
 * ========================LICENSE_START=================================
 * com.geewhiz.pacify.common
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

package com.geewhiz.pacify.utils;

import java.util.LinkedHashSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.geewhiz.pacify.defect.Defect;
import com.geewhiz.pacify.defect.DefectRuntimeException;



public class DefectUtils {

    private static Logger logger = LogManager.getLogger(DefectUtils.class.getName());

    public static void abortIfDefectExists(LinkedHashSet<Defect> defects) {
        if (defects.isEmpty()) {
            return;
        }

        logger.error("==== !!!!!! We got Errors !!!!! ...");
        for (Defect defect : defects) {
            logger.error(defect.getDefectMessage());
        }
        throw new DefectRuntimeException("We got errors... Aborting!");
    }
}
