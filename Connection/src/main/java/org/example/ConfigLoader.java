package org.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class ConfigLoader {
    private final String FILE_PATH = "Connection/src/main/resources/settings.txt";
    private final Properties properties = new Properties();

    public ConfigLoader() {
        try {
            properties.load(Files.newBufferedReader(Paths.get(FILE_PATH)));
        } catch (IOException e) {
            System.out.println("Port loading error, default port is used: 5000");
        }
    }

    public int getPort() {
        return Integer.parseInt(properties.getProperty("port", "5000"));
    }

    public String getServerHost() {
        return properties.getProperty("host", "localhost");
    }
}
