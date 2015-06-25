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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.geewhiz.pacify.defect.Defect;
import com.geewhiz.pacify.defect.PropertyDuplicateDefinedInPropertyFileDefect;
import com.geewhiz.pacify.resolver.BasePropertyResolver;
import com.geewhiz.pacify.utils.FileUtils;

public class FilePropertyResolver extends BasePropertyResolver {

    public static final String         IMPORT_STRING        = "#!import";
    public static final String         SEPARATOR_STRING     = "=";
    public static final String         COMMENT_STRING       = "#";

    public static final String         SEARCH_PATTERN       = "([^" + Pattern.quote(SEPARATOR_STRING) + "]*)" + Pattern.quote(SEPARATOR_STRING) + "?(.*)";
    public static final Pattern        PROPERTY_PATTERN     = Pattern.compile(SEARCH_PATTERN);

    private URL                        propertyFileURL;

    private boolean                    initialized          = false;
    private Map<String, String>        localProperties;
    private Map<String, String>        properties;
    private String                     fileEncoding         = "utf-8";
    private List<FilePropertyResolver> parentFileProperties = new ArrayList<FilePropertyResolver>();
    private String                     beginToken           = "%{";
    private String                     endToken             = "}";

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
        return getFileProperties().get(key);
    }

    /**
     * @return the localProperties for this instance
     */
    public Map<String, String> getLocalProperties() {
        if (!initialized) {
            initialize();
        }
        return localProperties;
    }

    public Set<String> getProperties() {
        if (!initialized) {
            initialize();
        }

        Set<String> result = new TreeSet<String>();

        for (String key : properties.keySet()) {
            result.add(key);
        }

        return result;
    }

    /**
     * @return the localProperties for this instance and its parents.
     */
    public Map<String, String> getFileProperties() {
        if (!initialized) {
            initialize();
        }
        return properties;
    }

    public String getPropertyResolverDescription() {
        return "FileResolver=" + getPropertyFileURL().toString();
    }

    public List<Defect> checkForDuplicateEntry() {
        List<Defect> defects = new ArrayList<Defect>();

        Set<String> propertyIds = new HashSet<String>();

        for (String line : FileUtils.getFileAsLines(getPropertyFileURL(), fileEncoding)) {
            if (line.startsWith("#")) {
                continue;
            }
            if (line.trim().isEmpty()) {
                continue;
            }

            String[] split = line.split("=");
            String propertyId = split[0];
            boolean couldBeAdded = propertyIds.add(propertyId);
            if (!couldBeAdded) {
                Defect defect = new PropertyDuplicateDefinedInPropertyFileDefect(propertyId, this);
                defects.add(defect);
            }
        }

        for (FilePropertyResolver parentFilePropertyContainer : getParentPropertyFileProperties()) {
            defects.addAll(parentFilePropertyContainer.checkForDuplicateEntry());
        }
        return defects;
    }

    public URL getPropertyFileURL() {
        return propertyFileURL;
    }

    public List<FilePropertyResolver> getParentPropertyFileProperties() {
        return parentFileProperties;
    }

    private void initialize() {
        initialized = true;
        loadPropertyFile(this);

        properties = new TreeMap<String, String>();
        for (FilePropertyResolver parentFilePropertyContainer : getParentPropertyFileProperties()) {
            Map<String, String> parentProperties = parentFilePropertyContainer.getFileProperties();
            properties.putAll(parentProperties);
        }
        properties.putAll(localProperties);
    }

    protected void setLocalProperties(Map<String, String> localProperties) {
        this.localProperties = localProperties;
    }

    protected void addParentPropertyFile(FilePropertyResolver parent) {
        parent.initialize();
        parentFileProperties.add(parent);
    }

    public void loadPropertyFile(FilePropertyResolver filePropertyResolver) {
        InputStreamReader isr = null;
        try {
            URL propertyFileURL = filePropertyResolver.getPropertyFileURL();

            isr = getInputStreamReaderFor(propertyFileURL);
            BufferedReader br = new BufferedReader(isr);
            Map<String, String> properties = new TreeMap<String, String>();

            for (String line; (line = br.readLine()) != null;) {
                if (line.length() == 0) {
                    continue;
                }
                if (line.startsWith(FilePropertyResolver.IMPORT_STRING)) {
                    String[] includes = line.substring(FilePropertyResolver.IMPORT_STRING.length()).trim().split(" ");
                    for (String include : includes) {
                        URL parentPropertyFileURL = new URL(propertyFileURL, include);
                        FilePropertyResolver parentFilePropertyContainer = new FilePropertyResolver(
                                parentPropertyFileURL);
                        filePropertyResolver.addParentPropertyFile(parentFilePropertyContainer);
                    }
                    continue;
                }
                if (line.startsWith(FilePropertyResolver.COMMENT_STRING)) {
                    continue;
                }

                Matcher matcher = PROPERTY_PATTERN.matcher(line);
                matcher.find();
                if (matcher.groupCount() != 2) {
                    throw new IllegalArgumentException("Property line isn't correct [" + line + "]");
                }
                properties.put(matcher.group(1), matcher.group(2));
            }
            filePropertyResolver.setLocalProperties(properties);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        finally {
            if (isr != null) {
                try {
                    isr.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    private InputStreamReader getInputStreamReaderFor(URL propertyFilePathURL) {
        InputStreamReader result;
        try {
            result = new InputStreamReader(propertyFilePathURL.openStream(), getEncoding());
        } catch (IOException e) {
            throw new RuntimeException("Couldn't find resource [" + propertyFilePathURL + "] in classpath.", e);
        }
        return result;
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
