package com.geewhiz.pacify.property.resolver.fileresolver;

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

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.geewhiz.pacify.defect.Defect;
import com.geewhiz.pacify.defect.PropertyDuplicateDefinedInPropertyFileDefect;
import com.geewhiz.pacify.property.resolver.fileresolver.exceptions.WrongPropertyLineRuntimeException;
import com.geewhiz.pacify.resolver.BasePropertyResolver;
import com.geewhiz.pacify.utils.FileUtils;

public class FilePropertyResolver extends BasePropertyResolver {

    public static final String         IMPORT_STRING       = "#!import";
    public static final String         SEPARATOR_STRING    = "=";
    public static final String         COMMENT_STRING      = "#";

    public static final String         SEARCH_PATTERN      = "([^" + Pattern.quote(SEPARATOR_STRING) + "]*)" + Pattern.quote(SEPARATOR_STRING) + "?(.*)";
    public static final Pattern        PROPERTY_PATTERN    = Pattern.compile(SEARCH_PATTERN);

    private boolean                    initialized         = false;

    private URL                        propertyFileURL;
    private Map<String, String>        localProperties     = new TreeMap<String, String>();
    private Map<String, String>        properties;
    private String                     fileEncoding        = "utf-8";
    private List<FilePropertyResolver> parents             = new ArrayList<FilePropertyResolver>();
    private String                     beginToken          = "%{";
    private String                     endToken            = "}";

    private Set<String>                duplicateProperties = new TreeSet<String>();

    public FilePropertyResolver(URL propertyFileURL) {
        this.propertyFileURL = propertyFileURL;
    }

    @Override
    public String getEncoding() {
        return fileEncoding;
    }

    public boolean containsProperty(String key) {
        return getPropertyValue(key) != null;
    }

    public String getPropertyValue(String key) {
        return getProperties().get(key);
    }

    /**
     * @return the localProperties for this instance, without the parents
     */
    public Map<String, String> getLocalProperties() {
        if (!initialized) {
            initialize();
        }
        return localProperties;
    }

    public Set<String> getPropertyKeys() {
        if (!initialized) {
            initialize();
        }

        return properties.keySet();
    }

    /**
     * @return the localProperties for this instance and its parents.
     */
    public Map<String, String> getProperties() {
        if (!initialized) {
            initialize();
        }
        return properties;
    }

    public String getPropertyResolverDescription() {
        return "FileResolver=" + getPropertyFileURL().toString();
    }

    public List<Defect> checkForDuplicateEntry() {
        if (!initialized) {
            initialize();
        }

        List<Defect> defects = new ArrayList<Defect>();

        for (FilePropertyResolver parentFilePropertyContainer : getParents()) {
            defects.addAll(parentFilePropertyContainer.checkForDuplicateEntry());
        }

        for (String duplicate : duplicateProperties) {
            Defect defect = new PropertyDuplicateDefinedInPropertyFileDefect(duplicate, this);
            defects.add(defect);
        }

        return defects;
    }

    public URL getPropertyFileURL() {
        return propertyFileURL;
    }

    public List<FilePropertyResolver> getParents() {
        return parents;
    }

    private void initialize() {
        initialized = true;

        properties = new TreeMap<String, String>();

        parsePropertyFile();

        for (FilePropertyResolver parents : getParents()) {
            Map<String, String> parentProperties = parents.getProperties();
            properties.putAll(parentProperties);
        }

        properties.putAll(getLocalProperties());
    }

    protected void setLocalProperties(Map<String, String> localProperties) {
        this.localProperties = localProperties;
    }

    protected void addParent(FilePropertyResolver parent) {
        parents.add(parent);
        parent.initialize();
    }

    private void parsePropertyFile() {
        for (String line : FileUtils.getFileAsLines(getPropertyFileURL(), getEncoding())) {
            if (line.length() == 0) {
                continue;
            }

            if (line.startsWith(FilePropertyResolver.IMPORT_STRING)) {
                String[] parents = line.substring(FilePropertyResolver.IMPORT_STRING.length()).trim().split(" ");
                for (String parentAsString : parents) {
                    URL parentUrl = FileUtils.getFileUrl(getPropertyFileURL(), parentAsString);
                    FilePropertyResolver parent = new FilePropertyResolver(parentUrl);
                    addParent(parent);
                }
                continue;
            }
            if (line.startsWith(FilePropertyResolver.COMMENT_STRING)) {
                continue;
            }

            Matcher matcher = PROPERTY_PATTERN.matcher(line);
            matcher.find();
            if (matcher.groupCount() != 2) {
                throw new WrongPropertyLineRuntimeException(line);
            }
            String key = matcher.group(1);
            String value = matcher.group(2);
            String allreadyAdded = localProperties.put(key, value);
            if (allreadyAdded != null) {
                duplicateProperties.add(key);
            }
        }
    }

    public void setEncoding(String encoding) {
        this.fileEncoding = encoding;
    }

    public void setBeginToken(String beginToken) {
        this.beginToken = beginToken;
    }

    public String getBeginToken() {
        return beginToken;
    }

    public void setEndToken(String endToken) {
        this.endToken = endToken;
    }

    public String getEndToken() {
        return endToken;
    }

}
