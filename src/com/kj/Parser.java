package com.kj;

import java.io.File;
import java.util.List;

class Parser {
    private final List<KJFile> files;

    enum STATE {
        NONE,
        ADDR,
        SRC,
        AFTER_SRC,
        DST,
        AFTER_DST,
        SPI,
        AFTER_SPI,
        AUTH,
        AUTH_ALG,
        AUTH_VAL,
        AFTER_AUTH,
        ENC,
        ENC_ALG,
        ENC_VAL
    }

    Parser(List<KJFile> files) {
        this.files = files;
    }

    void parse() {
        for (KJFile file : files) {
            parseFile(file);
        }
    }

    private void parseFile(KJFile file) {
        if (file == null) {
            return;
        }
        String fileName = file.getFileName();

        if (fileName.contains(".log") || fileName.contains(".txt")) {
            parseMainLog(file);
        }
    }

    private void parseMainLog(KJFile file) {
        File logFile = new File(file.getFileNameAbsolutePath());

        MainLog log = new MainLog();
        log.parse(logFile);
        file.setKeys(log.getKeys());
    }
}
