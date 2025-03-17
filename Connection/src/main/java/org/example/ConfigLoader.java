package org.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class ConfigLoader {
    private final String FILE_PATH = "src/main/resources/settings.txt";

    public int loadPort() {
        Properties properties = new Properties();
        try {
            properties.load(Files.newBufferedReader(Paths.get(FILE_PATH)));
            return Integer.parseInt(properties.getProperty("port"));
        } catch (IOException | NumberFormatException e) {
            System.out.println("Port loading error, default port is used: 5000");
            return 5000;
        }
    }
}
