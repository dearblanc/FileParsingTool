package com.kj;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

class KJFile {
    private final File file;
    private List<KJFile> child = new ArrayList<>();
    private List<Key> keys = new ArrayList<>();

    KJFile(File file) {
        this.file = file;
    }

    public List<KJFile> getChildFiles() {
        try {
            return List.of(Objects.requireNonNull(file.listFiles()))
                    .stream()
                    .filter(File::isFile)
                    .filter(File::canRead)
                    .map(KJFile::new)
                    .filter(KJFile::isLogFile)
                    .collect(Collectors.toList());
        } catch (NullPointerException e) {
            e.printStackTrace();
            return List.of();
        }
    }

    public List<KJFile> getChildDirectories() {
        try {
            return List.of(Objects.requireNonNull(file.listFiles()))
                    .stream()
                    .filter(File::isDirectory)
                    .map(KJFile::new)
                    .collect(Collectors.toList());
        } catch (NullPointerException e) {
            e.printStackTrace();
            return List.of();
        }
    }

    public List<Key> getKeys() {
        return keys;
    }

    public void setKeys(Key[] arrKey) {
        keys = List.of(arrKey);
    }

    private boolean isLogFile() {
        return true;
    }
}
