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



import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;

public class FileUtils {

    private static final boolean IS_POSIX = FileSystems.getDefault().supportedFileAttributeViews().contains("posix");

    public static List<String> getFileAsLines(URL fileURL, String encoding) {
        InputStream is = null;
        try {
            is = fileURL.openStream();
            return IOUtils.readLines(is, Charsets.toCharset(encoding));
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    public static URL getFileUrl(File file) {
        try {
            return file.toURI().toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public static URL getFileUrl(URL url, String file) {
        try {
            return new URL(url, file);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public static File createEmptyFileWithSamePermissions(File forFile) {
        return createEmptyFileWithSamePermissions(forFile, forFile.getName());
    }

    public static File createEmptyFileWithSamePermissions(File forFile, String filePrefix) {
        try {
            File folder = forFile.getParentFile();

            File tmp = File.createTempFile(filePrefix, ".tmp", folder);

            if (IS_POSIX) {
                Set<PosixFilePermission> attrs = Files.getPosixFilePermissions(Paths.get(forFile.toURI()));
                Files.setPosixFilePermissions(Paths.get(tmp.toURI()), attrs);
            }

            return tmp;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getFileInOneString(File file, String encoding) {
        InputStream is = null;
        try {
            is = new FileInputStream(file);
            return IOUtils.toString(is, Charsets.toCharset(encoding));
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

}
