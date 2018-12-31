package com.kj;

class Parser {

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

    KJFile parse(KJFile file) {
        KJLog log = new KJLog();
        log.parse(file);
        return file;
    }
}
