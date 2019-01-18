package com.kj.enc;

import java.util.IllegalFormatException;
import java.util.StringTokenizer;

class SPI {
    static String parseSPI(String line) {
        StringTokenizer tokenizer = new StringTokenizer(line);
        String spi = null;

        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();

            if (token.equalsIgnoreCase("spi")) {
                if (!tokenizer.hasMoreTokens()) {
                    return null;
                }

                spi = tokenizer.nextToken();
                spi = convertSpiToHexadecimalIfNeed(spi);
                break;
            }
        }

        return spi;
    }

    static String convertSpiToHexadecimalIfNeed(String decimalSpi) {
        if (decimalSpi == null) {
            return null;
        }

        if (decimalSpi.startsWith("0x")) {
            return decimalSpi;
        }

        String hexSpi = null;
        try {
            int decimal = Integer.parseInt(decimalSpi);
            hexSpi = String.format(("0x%08x"), decimal);
        } catch (NumberFormatException | IllegalFormatException e) {
            e.printStackTrace();
        }

        return hexSpi;
    }
}
