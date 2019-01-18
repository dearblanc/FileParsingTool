package com.kj.enc;

import com.kj.KJFile;
import com.kj.temp.KJLog;

public class Parser {

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

    public void parse(KJFile file) {
        KJLog log = new KJLog();
        log.parse(file);
    }
}
