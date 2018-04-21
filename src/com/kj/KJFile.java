package com.kj;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
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
            return List.of(file.listFiles())
                    .stream()
                    .filter(f -> f.isFile() && f.canRead())
                    .map(f -> new KJFile(f))
                    .filter(f -> f.isLogFile())
                    .collect(Collectors.toList());
        } catch (NullPointerException e) {
            e.printStackTrace();
            return List.of();
        }
    }

    public List<KJFile> getChildDirectories() {
        try {
            return List.of(file.listFiles())
                    .stream()
                    .filter(f -> f.isDirectory())
                    .map(f -> new KJFile(f))
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
