package com.kj;

import java.util.IllegalFormatException;
import java.util.StringTokenizer;

class Authentication {
    private String key = null;
    private String algorithm = null;

    static Authentication parseAuthentication(String line) {
        StringTokenizer tokenizer = new StringTokenizer(line);
        Authentication auth = new Authentication();

        if (!tokenizer.hasMoreTokens()) {
            return null;
        }

        String token = tokenizer.nextToken();
        if (!(token.equalsIgnoreCase("auth-trunc") || token.equalsIgnoreCase("\tauth-trunc"))) {
            return null;
        }

        if (!tokenizer.hasMoreTokens()) {
            return null;
        }

        auth.setAlgorithm(tokenizer.nextToken());

        if (!tokenizer.hasMoreTokens()) {
            return null;
        }

        auth.setKey(tokenizer.nextToken());
        String algorithm = WireShark.adaptAuthAlgorithm(auth.getAlgorithm());
        auth.setAlgorithm(algorithm);
        if (algorithm != null) {
            if (algorithm.equalsIgnoreCase("NULL")) {
                auth.setKey("null");
            }
        } else {
            if (!tokenizer.hasMoreTokens()) {
                return null;
            }
            token = tokenizer.nextToken();

            try {
                int decimal = Integer.parseInt(token);
                auth.setAlgorithm(
                        String.format("ANY %d bit authentication [no checking]", decimal));
            } catch (NumberFormatException | IllegalFormatException e) {
                return null;
            }
        }

        return auth;
    }

    void setKey(String key) {
        this.key = key;
    }

    String getKey() {
        return key;
    }

    void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    String getAlgorithm() {
        return algorithm;
    }
}
