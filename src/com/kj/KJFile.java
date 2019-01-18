package com.kj;

import com.kj.enc.Key;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.PatternSyntaxException;

public class KJFile {
    private final String fileName;
    private List<Key> keys = new ArrayList<>();

    KJFile(File file) {
        fileName = file.getAbsolutePath();
    }

    public String getFileNameAbsolutePath() {
        return fileName;
    }

    public List<Key> getKeys() {
        return keys;
    }

    public void setKeys(List<Key> keys) {
        this.keys = keys;
    }

    boolean isIntendedFile() {
        String name;
        String ext;
        try {
            String[] tokens = getFileName().split("\\.");
            if (tokens.length < 2) {
                return false;
            }
            name = tokens[0];
            ext = tokens[1];
        } catch (ArrayIndexOutOfBoundsException | PatternSyntaxException e) {
            e.printStackTrace();
            return false;
        }

        return name != null && ext != null; // add logic
    }

    private String getFileName() {
        String[] splitToken;
        try {
            splitToken = fileName.split("\\\\");
        } catch (PatternSyntaxException e) {
            e.printStackTrace();
            return "";
        }
        return splitToken[splitToken.length - 1];
    }

    long getFileSize() {
        return new File(fileName).length();
    }

    static List<File> getFiles(File parent) {
        List<File> files = getChildren(parent);
        files.removeIf(file -> (!file.isFile() || !file.canRead()));
        return files;
    }

    static List<File> getDirectories(File parent) {
        List<File> files = getChildren(parent);
        files.removeIf(file -> (!file.isDirectory()));
        return files;
    }

    private static List<File> getChildren(File parent) {
        List<File> list = new ArrayList<>();
        File[] files = parent.listFiles();
        if (files == null) {
            return list;
        }
        list.addAll(List.of(files));
        return list;
    }
}
