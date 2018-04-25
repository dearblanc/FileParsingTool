package com.kj;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.PatternSyntaxException;

class KJFile {
    private final String fileName;
    private List<Key> keys = new ArrayList<>();

    KJFile(File file) {
        fileName = file.getAbsolutePath();
    }

    String getFileNameAbsolutePath() {
        return fileName;
    }

    List<Key> getKeys() {
        return keys;
    }

    void setKeys(List<Key> keys) {
        this.keys = keys;
    }

    boolean isLogFile() {
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

        if (name == null || ext == null) {
            return false;
        }

        if (EtcFiles.getInstance().etc(name)) {
            return false;
        }

        if (ext.startsWith("pcap")) {
            return false;
        } else if (ext.startsWith("db")) {
            return false;
        } else if (ext.startsWith("xml")) {
            return false;
        } else if (name.startsWith("event")) {
            return false;
        } else if (name.toLowerCase().startsWith("system")) {
            return false;
        } else if (name.startsWith("pstore")) {
            return false;
        } else if (name.startsWith("tombstone")) {
            return false;
        } else if (name.startsWith("dump")) {
            return false;
        } else if (name.startsWith("storage")) {
            return false;
        } else if (name.startsWith("cnss")) {
            return false;
        } else if (name.startsWith("platform")) {
            return false;
        } else if (name.startsWith("host")) {
            return false;
        } else if (name.startsWith("ramoops")) {
            return false;
        } else if (name.startsWith("dropbox")) {
            return false;
        }

        return true;
    }

    private static class EtcFiles {
        private static EtcFiles instance = null;
        private String[] blackList =
                new String[] {
                    "bugreport-traces",
                    "traces",
                    "events",
                    "kernel",
                    "matics",
                    "modem_debug_info",
                    "packet",
                    "radio",
                    "system",
                    "memory",
                    "power",
                    "fg",
                    "crash"
                };

        static EtcFiles getInstance() {
            if (instance == null) {
                instance = new EtcFiles();
            }

            return instance;
        }

        private EtcFiles() {
            init();
        }

        boolean etc(String fileName) {
            return Arrays.binarySearch(blackList, fileName) >= 0;
        }

        private void init() {
            Arrays.sort(blackList);
        }
    }

    String getFileName() {
        String[] splitToken;
        try {
            splitToken = fileName.split("\\\\");
        } catch (PatternSyntaxException e) {
            e.printStackTrace();
            return "";
        }
        return splitToken[splitToken.length - 1];
    }

    static BufferedReader openFileReader(File file) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return reader;
    }

    static List<File> getChildDirectories(File parent) {
        List<File> files = getChildren(parent);
        files.removeIf(file -> !file.isDirectory());
        return files;
    }

    static List<File> getChildFiles(File parent) {
        List<File> files = getChildren(parent);
        files.removeIf(file -> (!file.isFile() || !file.canRead()));
        return files;
    }

    private static List<File> getChildren(File parent) {
        List<File> list = new ArrayList<>();
        if (parent == null) {
            return list;
        }
        File[] files = parent.listFiles();
        if (files == null) {
            return list;
        }
        list.addAll(List.of(files));
        return list;
    }
}
