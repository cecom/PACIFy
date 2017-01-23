/*-
 * ========================LICENSE_START=================================
 * com.geewhiz.pacify.impl
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

package com.geewhiz.pacify.filter;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.io.output.FileWriterWithEncoding;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;

import com.geewhiz.pacify.defect.Defect;
import com.geewhiz.pacify.defect.WrongTokenDefinedDefect;
import com.geewhiz.pacify.model.PFile;
import com.geewhiz.pacify.utils.FileUtils;

public class PacifyVelocityFilter implements PacifyFilter {

    private static final String BEGIN_TOKEN = "${";
    private static final String END_TOKEN   = "}";

    @Override
    public LinkedHashSet<Defect> filter(PFile pFile, Map<String, String> propertyValues) {
        LinkedHashSet<Defect> defects = new LinkedHashSet<Defect>();

        if (!BEGIN_TOKEN.equals(pFile.getBeginToken())) {
            defects.add(
                    new WrongTokenDefinedDefect(pFile, "If you use the PacifyVelocityFilter class, only \"" + BEGIN_TOKEN + "\" is allowed as start token."));
        }

        if (!END_TOKEN.equals(pFile.getEndToken())) {
            defects.add(new WrongTokenDefinedDefect(pFile, "If you use the PacifyVelocityFilter class, only \"" + END_TOKEN + "\" is allowed as end token."));
        }

        if (!defects.isEmpty()) {
            return defects;
        }

        File fileToFilter = pFile.getFile();
        File tmpFile = FileUtils.createEmptyFileWithSamePermissions(fileToFilter);

        Template template = getTemplate(fileToFilter, pFile.getEncoding());
        Context context = getContext(propertyValues, fileToFilter);

        try {
            FileWriterWithEncoding fw = new FileWriterWithEncoding(tmpFile, pFile.getEncoding());
            template.merge(context, fw);
            fw.close();
            if (!fileToFilter.delete()) {
                throw new RuntimeException("Couldn't delete file [" + fileToFilter.getPath() + "]... Aborting!");
            }
            if (!tmpFile.renameTo(fileToFilter)) {
                throw new RuntimeException("Couldn't rename filtered file from [" + tmpFile.getPath() + "] to [" + fileToFilter.getPath() + "]... Aborting!");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return defects;
    }

    private Template getTemplate(File file, String encoding) {
        Properties prop = new Properties();
        prop.put("file.resource.loader.path", file.getParentFile().getAbsolutePath());

        VelocityEngine ve = new VelocityEngine();
        ve.init(prop);

        Template template = ve.getTemplate(file.getName(), encoding);
        return template;
    }

    private Context getContext(Map<String, String> propertyValues, File file) {
        Context context = new VelocityContext();
        for (Entry<String, String> entry : propertyValues.entrySet()) {
            addProperty(context, entry.getKey(), entry.getValue());
        }
        return context;
    }

    @SuppressWarnings("unchecked")
    private void addProperty(Context context, String propertyName, String propertyValue) {
        String[] split = propertyName.split("\\.");

        if (split.length == 1) {
            context.put(propertyName, propertyValue);
            return;
        }

        Map<String, Object> lastNode = null;
        for (int i = 0; i < split.length - 1; i++) {
            String level = split[i];
            if (lastNode == null) {
                if (context.get(level) == null) {
                    context.put(level, new HashMap<String, Object>());
                }
                lastNode = (Map<String, Object>) context.get(level);
                continue;
            }
            if (lastNode.get(level) == null) {
                lastNode.put(level, new HashMap<String, Object>());
            }

            lastNode = (Map<String, Object>) lastNode.get(split[i]);
        }

        lastNode.put(split[split.length - 1], propertyValue);
    }

    @Override
    public LinkedHashSet<Defect> checkForNotReplacedTokens(PFile pFile) {
        // TODO: need to implement for velocity
        return new LinkedHashSet<Defect>();
    }

    @Override
    public LinkedHashSet<Defect> checkPlaceHolderExists(PFile pFile) {
        // TODO: need to implement for velocity
        return new LinkedHashSet<Defect>();
    }

}
