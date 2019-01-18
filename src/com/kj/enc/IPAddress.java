package com.kj.enc;

import java.util.StringTokenizer;

class IPAddress {

    enum VERSION {
        IPv4,
        IPv6
    }

    private VERSION version = VERSION.IPv4;
    private String src = null;
    private String dest = null;

    IPAddress() {}

    String getVersionString() {
        if (version == VERSION.IPv6) {
            return "IPv6";
        }
        return "IPv4";
    }

    void setSrc(String src) {
        this.src = src;
    }

    String getSrc() {
        return src;
    }

    void setDest(String dest) {
        this.dest = dest;
    }

    String getDest() {
        return dest;
    }

    static IPAddress parseAddress(String line) {
        IPAddress address = new IPAddress();
        StringTokenizer tokenizer = new StringTokenizer(line);

        if (tokenizer.countTokens() != 4) {
            return null;
        }

        int i = 0;
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            switch (i) {
                case 0:
                    if (!token.equalsIgnoreCase("src")) {
                        return null;
                    }
                    break;
                case 1:
                    address.setSrc(token);
                    break;
                case 3:
                    address.setDest(token);
                    break;
            }
            i++;
        }

        address.defineIPAddress();

        return address;
    }

    boolean isAddressValid() {
        if (src == null || dest == null) {
            return false;
        }
        return src.length() >= 5 && dest.length() >= 5;
    }

    void defineIPAddress() {
        if (!isAddressValid()) {
            return;
        }

        if (src.indexOf('.') != -1) {
            version = VERSION.IPv4;
        } else {
            version = VERSION.IPv6;
            src = normalizeIPv6Impl(src);
            dest = normalizeIPv6Impl(dest);
        }
    }

    private String normalizeIPv6Impl(String address) {
        if (address == null) {
            return null;
        }

        String result = address;

        if ((address.length() - address.replace(":", ":").length()) == 7) {
            result = address.replace("::", ":0:");

            if (result.charAt(0) == ':') {
                result = '0' + result;
            }
            if (result.charAt(result.length() - 1) == ':') {
                result = result + '0';
            }
        }

        return result;
    }
}
