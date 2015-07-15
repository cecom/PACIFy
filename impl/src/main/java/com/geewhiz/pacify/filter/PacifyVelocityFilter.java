package com.geewhiz.pacify.filter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.output.FileWriterWithEncoding;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;

import com.geewhiz.pacify.defect.Defect;
import com.geewhiz.pacify.defect.WrongTokenDefinedDefect;
import com.geewhiz.pacify.managers.PropertyResolveManager;
import com.geewhiz.pacify.model.PFile;
import com.geewhiz.pacify.model.PMarker;
import com.geewhiz.pacify.model.PProperty;

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

public class PacifyVelocityFilter implements PacifyFilter {

    private static final String BEGIN_TOKEN = "${";
    private static final String END_TOKEN   = "}";

    private Logger              logger      = LogManager.getLogger(PacifyVelocityFilter.class.getName());

    @Override
    public List<Defect> filter(PropertyResolveManager propertyResolveManager, PMarker pMarker, PFile pFile) {
        List<Defect> defects = new ArrayList<Defect>();

        if (!BEGIN_TOKEN.equals(pMarker.getBeginTokenFor(pFile))) {
            defects.add(new WrongTokenDefinedDefect(pMarker, pFile,
                    "If you use the PacifyVelocityFilter class, only \"" + BEGIN_TOKEN + "\" is allowed as start token."));
        }

        if (!END_TOKEN.equals(pMarker.getEndTokenFor(pFile))) {
            defects.add(new WrongTokenDefinedDefect(pMarker, pFile,
                    "If you use the PacifyVelocityFilter class, only \"" + END_TOKEN + "\" is allowed as end token."));
        }

        File file = pMarker.getAbsoluteFileFor(pFile);
        File tmpFile = new File(file.getParentFile(), file.getName() + "_tmp");

        Template template = getTemplate(pMarker, pFile);
        Context context = getContext(propertyResolveManager, pFile);

        try {
            FileWriterWithEncoding fw = new FileWriterWithEncoding(tmpFile, pFile.getEncoding());
            template.merge(context, fw);
            fw.close();
            if (!file.delete()) {
                throw new RuntimeException("Couldn't delete file [" + file.getPath() + "]... Aborting!");
            }
            if (!tmpFile.renameTo(file)) {
                throw new RuntimeException("Couldn't rename filtered file from [" + tmpFile.getPath() + "] to ["
                        + file.getPath() + "]... Aborting!");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return defects;
    }

    private Template getTemplate(PMarker pMarker, PFile pFile) {
        Properties prop = new Properties();
        prop.put("file.resource.loader.path", pMarker.getFolder().getAbsolutePath());

        VelocityEngine ve = new VelocityEngine();
        ve.init(prop);

        Template template = ve.getTemplate(pFile.getRelativePath(), pFile.getEncoding());
        return template;
    }

    private Context getContext(PropertyResolveManager propertyResolveManager, PFile pFile) {
        Context context = new VelocityContext();
        for (PProperty pProperty : pFile.getPProperties()) {
            String propertyName = pProperty.getName();
            String propertyValue = propertyResolveManager.getPropertyValue(pProperty);

            addProperty(context, propertyName, propertyValue);
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

}
