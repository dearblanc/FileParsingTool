package com.kj;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

class MainLog {
    private List<Key> keys = new ArrayList<>();
    private Key key = null;

    MainLog() {}

    List<Key> getKeys() {
        return keys;
    }

    void parse(File file) {
        final BufferedReader reader = KJFile.openFileReader(file);
        if (reader == null) {
            return;
        }
        String line = null;
        try {
            line = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        while (!Thread.currentThread().isInterrupted() && line != null) {
            // add logic

            try {
                line = reader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }

        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
