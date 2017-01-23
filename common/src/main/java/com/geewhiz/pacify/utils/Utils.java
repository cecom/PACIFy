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

import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.geewhiz.pacify.defect.DefectException;
import com.geewhiz.pacify.defect.FilterNotFoundDefect;
import com.geewhiz.pacify.filter.PacifyFilter;
import com.geewhiz.pacify.model.PFile;

public class Utils {

    private static Logger logger = LogManager.getLogger(Utils.class.getName());

    public static String getJarVersion() {
        URL jarURL = Utils.class.getResource("/" + Utils.class.getName().replace(".", "/") + ".class");
        Manifest mf;
        try {
            JarURLConnection jurlConn;
            if (jarURL.getProtocol().equals("file")) {
                return "Not a Jar";
            } else {
                jurlConn = (JarURLConnection) jarURL.openConnection();
            }
            mf = jurlConn.getManifest();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Attributes attr = mf.getMainAttributes();
        return attr.getValue("Implementation-Version");
    }

    public static PacifyFilter getPacifyFilter(PFile pFile) throws DefectException {
        String filterClass = pFile.getFilterClass();

        try {
            PacifyFilter filter = (PacifyFilter) Class.forName(filterClass).newInstance();
            return filter;
        } catch (Exception e) {
            logger.debug("Error while instantiate filter class [" + filterClass + "]", e);
            throw new FilterNotFoundDefect(pFile);
        }

    }
}
