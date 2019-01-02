package com.kj;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

class FileTraversal {
    private final List<File> rawFiles = new ArrayList<>();

    void loadFiles(List<File> files) {
        files.forEach(this::addFileWithChildren);
    }

    private void addFileWithChildren(File file) {
        if (file.isFile()) {
            rawFiles.add(file);
        }
        rawFiles.addAll(KJFile.getFiles(file));
        loadFiles(KJFile.getDirectories(file));
    }

    List<KJFile> retrieveAllFiles() {
        return rawFiles.stream().map(KJFile::new).collect(Collectors.toList());
    }

    long totalFileSize() {
        AtomicLong total = new AtomicLong(0L);
        rawFiles.forEach(f -> total.addAndGet(f.length()));
        return total.get();
    }
}
