package com.kj;

class Key {
    private IPAddress address;
    private String spi;
    private Authentication auth;
    private Encryption encryption;

    Key() {}

    String printKey() {
        String key = null;

        try {
            key =
                    "\""
                            + address.getVersionString()
                            + "\",\""
                            + address.getSrc()
                            + "\",\""
                            + address.getDest()
                            + "\",\""
                            + spi
                            + "\",\""
                            + encryption.getAlgorithm()
                            + "\",\""
                            + encryption.getKey()
                            + "\",\""
                            + auth.getAlgorithm()
                            + "\",\""
                            + auth.getKey()
                            + "\"";
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        return key;
    }

    String getSpi() {
        return spi;
    }

    void setIPAddress(IPAddress ipAddress) {
        address = ipAddress;
    }

    IPAddress getIPAddress() {
        return address;
    }

    void setSpi(String spi) {
        this.spi = spi;
    }

    void setAuth(Authentication auth) {
        this.auth = auth;
    }

    Authentication getAuth() {
        return auth;
    }

    void setEncryption(Encryption encryption) {
        this.encryption = encryption;
    }

    Encryption getEncryption() {
        return encryption;
    }
}
