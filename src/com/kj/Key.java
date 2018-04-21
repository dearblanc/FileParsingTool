package com.kj;

class Key {
    private String key1 = null;
    private String key2 = null;

    public Key() {
    }

    public Key(String value1, String value2) {
        this.key1 = value1;
        this.key2 = value2;
    }

    public String getKey1() {
        return key1;
    }

    public void setKey1(String value) {
        key1 = value;
    }

    public String getKey2() {
        return key2;
    }

    public void setKey2(String value) {
        key2 = value;
    }
}
