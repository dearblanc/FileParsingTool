package com.kj;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class SharedFileList {
    private static final SharedFileList instance = new SharedFileList();

    private List<KJFile> list = Collections.synchronizedList(new ArrayList<>());

    private SharedFileList() {}

    static SharedFileList getInstance() {
        return instance;
    }

    void addAll(List<KJFile> files) {
        list.addAll(files);
    }

    KJFile take() {
        if (list.isEmpty()) return null;
        return list.remove(0);
    }
}
