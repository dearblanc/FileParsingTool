package com.kj;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class Parser {
    private static Random random = new Random();
    private final List<KJFile> files;

    Parser(List<KJFile> files) {
        this.files = files;
    }

    public void parse() {
        /* add logic */
        for (KJFile file : files) {
            file.setKeys(getKeyTempCode());
        }
    }

    private List<Key> getKeyTempCode() {
        List<Key> keys = new ArrayList<>();

        if (random.nextBoolean()) {
            int count = random.nextInt(11);
            for (int i = 0; i < count; i++) {
                Key key = new Key();
                key.setId(String.valueOf(random.nextInt()));
                key.setKey1(String.valueOf(random.nextInt(1000)));
                key.setKey2(String.valueOf(random.nextInt(1000)));
                keys.add(key);
            }
        }
        return keys;
    }
}
