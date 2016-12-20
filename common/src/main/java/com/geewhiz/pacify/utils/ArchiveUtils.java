package com.geewhiz.pacify.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.io.IOUtils;

public class ArchiveUtils {

    public static boolean isArchiveAndIsSupported(String archiveName) {
        String type = getArchiveType(archiveName);

        if ("jar".equalsIgnoreCase(type)) {
            return true;
        }
        if ("zip".equalsIgnoreCase(type)) {
            return true;
        }
        if ("tar".equalsIgnoreCase(type)) {
            return true;
        }
        return false;
    }

    public static File extractFile(File archive, String file) {
        return extractFiles(archive, file, false).get(0);
    }

    public static List<File> extractFilesForRegExp(File archive, String regexpToMatch) {
        return extractFiles(archive, regexpToMatch, true);
    }

    ////////////////////////////// PRIVAT Stuff
    private static List<File> extractFiles(File archive, String searchFor, Boolean isRegExp) {
        List<File> result = new ArrayList<File>();

        ArchiveInputStream ais = null;
        try {
            ArchiveStreamFactory factory = new ArchiveStreamFactory();

            ais = factory.createArchiveInputStream(getArchiveType(archive.getName()), new FileInputStream(archive));

            ArchiveEntry entry;
            while ((entry = ais.getNextEntry()) != null) {
                if (isRegExp) {
                    if (!matches(entry.getName(), searchFor)) {
                        continue;
                    }
                } else if (!searchFor.equals(entry.getName())) {
                    continue;
                }

                File physicalFile = FileUtils.createEmptyFileWithSamePermissions(archive,
                        archive.getName() + "!" + Paths.get(entry.getName()).getFileName().toString() + "_");

                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(physicalFile));

                byte[] content = new byte[2048];

                int len;
                while ((len = ais.read(content)) != -1) {
                    bos.write(content, 0, len);
                }

                bos.close();
                content = null;

                result.add(physicalFile);
            }
        } catch (ArchiveException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(ais);
        }

        return result;
    }

    // TODO: fileutils contains same method
    private static Boolean matches(String pathName, String regExp) {
        Path path = FileSystems.getDefault().getPath(pathName);
        PathMatcher matcher = FileSystems.getDefault().getPathMatcher("regex:" + regExp);
        return matcher.matches(path);
    }

    private static String getArchiveType(String archiveName) {
        int idx = archiveName.lastIndexOf(".");
        String type = archiveName.substring(idx + 1);

        if ("jar".equalsIgnoreCase(type)) {
            return ArchiveStreamFactory.JAR;
        }
        if ("war".equalsIgnoreCase(type)) {
            return ArchiveStreamFactory.JAR;
        }
        if ("ear".equalsIgnoreCase(type)) {
            return ArchiveStreamFactory.JAR;
        }
        if ("zip".equalsIgnoreCase(type)) {
            return ArchiveStreamFactory.ZIP;
        }
        if ("tar".equalsIgnoreCase(type)) {
            return ArchiveStreamFactory.TAR;
        }

        return type;
    }

}
