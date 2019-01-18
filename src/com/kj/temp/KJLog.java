package com.kj.temp;

import com.kj.KJFile;
import com.kj.enc.Key;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class KJLog {
    private List<Key> keys = new ArrayList<>();

    public void parse(KJFile file) {
        file.setKeys(keys);

        try (Scanner scanner = new Scanner(new FileInputStream(file.getFileNameAbsolutePath()))) {

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.contains("test") || line.contains("sefw")) {
                    keys.add(new Key());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
