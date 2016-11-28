package com.geewhiz.pacify.model.utils;

import static java.nio.file.FileVisitResult.CONTINUE;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import com.geewhiz.pacify.model.PFile;

public class PFileFinder extends SimpleFileVisitor<Path> {

    private final PathMatcher matcher;

    private List<Path>        parents;
    private List<String>      foundFiles;

    PFileFinder(PFile pFile) {
        matcher = FileSystems.getDefault().getPathMatcher("regex:" + pFile.getRelativePath());
        foundFiles = new ArrayList<String>();
        parents = new ArrayList<Path>();
    }

    void evaluate(Path file) {
        Path name = file.getFileName();
        if (name != null && matcher.matches(name)) {
            String finalName = "";
            for (Path parent : parents) {
                if (parent.equals(parents.get(0))) {
                    // first parent is the folder of the marker file, skip this
                    continue;
                }
                if (!parent.equals(parents.get(1)))
                    finalName += "/";
                finalName += parent.getFileName().toString();
            }
            if (parents.size() > 1) {
                finalName += "/";
            }
            finalName += name.getFileName().toString();
            foundFiles.add(finalName);
        }
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
        evaluate(file);
        return CONTINUE;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
        parents.add(dir);
        evaluate(dir);
        return CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
        parents.remove(parents.size() - 1);
        return CONTINUE;
    }

    public List<String> getFiles() {
        return foundFiles;
    }

}
