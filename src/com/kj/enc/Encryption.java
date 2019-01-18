package com.kj.enc;

import com.kj.WireShark;

import java.util.StringTokenizer;

class Encryption {
    private String key = null;
    private String algorithm = null;

    static Encryption parseEncryption(String line) {
        Encryption encryption = new Encryption();
        StringTokenizer tokenizer = new StringTokenizer(line);

        if (!tokenizer.hasMoreTokens()) {
            return null;
        }
        String token = tokenizer.nextToken();

        if (!(token.equalsIgnoreCase("enc") || token.equalsIgnoreCase("\tenc"))) {
            return null;
        }
        if (!tokenizer.hasMoreTokens()) {
            return null;
        }

        encryption.setAlgorithm(tokenizer.nextToken());

        if (!tokenizer.hasMoreTokens()) {
            return null;
        }

        encryption.setKey(tokenizer.nextToken());
        String algorithm = WireShark.adaptEncryptionAlgorithm(encryption.getAlgorithm());

        if (algorithm != null && algorithm.equalsIgnoreCase("NULL")) {
            encryption.setKey("null");
        }

        return encryption;
    }

    String getKey() {
        return key;
    }

    void setKey(String key) {
        this.key = key;
    }

    String getAlgorithm() {
        return algorithm;
    }

    void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }
}
