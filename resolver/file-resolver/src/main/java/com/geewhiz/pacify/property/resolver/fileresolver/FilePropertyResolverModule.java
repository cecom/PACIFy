/*-
 * ========================LICENSE_START=================================
 * com.geewhiz.pacify.resolver.file-resolver
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

package com.geewhiz.pacify.property.resolver.fileresolver;

import java.io.File;
import java.net.URL;
import java.util.LinkedHashSet;
import java.util.Map;

import com.geewhiz.pacify.defect.Defect;
import com.geewhiz.pacify.property.resolver.fileresolver.defects.PropertyFileNotDefinedDefect;
import com.geewhiz.pacify.property.resolver.fileresolver.defects.PropertyFileNotFoundDefect;
import com.geewhiz.pacify.resolver.PropertyResolver;
import com.geewhiz.pacify.resolver.PropertyResolverModule;
import com.geewhiz.pacify.utils.FileUtils;
import com.google.inject.Provides;
import com.google.inject.multibindings.Multibinder;



public class FilePropertyResolverModule extends PropertyResolverModule {
    private Map<String, String> commandLineParameters;
    private URL                 fileUrl;
    private LinkedHashSet<Defect>        defects = new LinkedHashSet<Defect>();

    @Override
    public String getResolverId() {
        return "FileResolver";
    }

    @Override
    protected void configure() {
        Multibinder<PropertyResolver> resolveBinder = Multibinder.newSetBinder(binder(), PropertyResolver.class);
        resolveBinder.addBinding().to(FilePropertyResolver.class);
    }

    @Override
    public void setParameters(Map<String, String> commandLineParameters) {
        this.commandLineParameters = commandLineParameters;

        String propertyFile = commandLineParameters.get("file");
        if (propertyFile == null) {
            defects.add(new PropertyFileNotDefinedDefect());
            return;
        }

        this.fileUrl = getFileUrl(propertyFile);
        if (fileUrl == null) {
            defects.add(new PropertyFileNotFoundDefect(propertyFile));
            return;
        }
    }

    @Override
    public LinkedHashSet<Defect> getDefects() {
        return defects;
    }

    @Provides
    public FilePropertyResolver createFilePropertyResolver() {
        FilePropertyResolver filePropertyResolver = new FilePropertyResolver(fileUrl);
        if (commandLineParameters.containsKey("beginToken")) {
            filePropertyResolver.setBeginToken(commandLineParameters.get("beginToken"));
        }
        if (commandLineParameters.containsKey("endToken")) {
            filePropertyResolver.setEndToken(commandLineParameters.get("endToken"));
        }
        if (commandLineParameters.containsKey("encoding")) {
            filePropertyResolver.setEncoding(commandLineParameters.get("encoding"));
        }
        return filePropertyResolver;
    }

    private URL getFileUrl(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            if (file.isFile()) {
                return FileUtils.getFileUrl(file);
            }
            return null;
        }

        URL url = this.getClass().getClassLoader().getResource(filePath);
        if (url != null) {
            return url;
        }

        return null;
    }

}
