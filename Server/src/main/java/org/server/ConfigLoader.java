package org.server;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class ConfigLoader {
    public int loadPort(String filename) {
        Properties properties = new Properties();
        try {
            properties.load(Files.newBufferedReader(Paths.get(filename)));
            return Integer.parseInt(properties.getProperty("port"));
        } catch (IOException | NumberFormatException e) {
            System.out.println("Ошибка загрузки порта, используется порт по умолчанию: 5000");
            return 5000;
        }
    }
}
