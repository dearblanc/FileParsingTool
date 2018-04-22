package com.kj;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class KJFile {
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

    public boolean isIntendedFile() {
        /* add logic */
        /* this is temp code */
        return new Random().nextBoolean();
    }
}
