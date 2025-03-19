package org.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class ConfigLoader {
    private final String FILE_PATH = "Connection/src/main/resources/settings.txt";
    private static final int MIN_PORT = 0;
    private static final int MAX_PORT = 65535;

    private final MyLogger logger = MyLogger.getLogger();
    private final Properties properties = new Properties();

    public ConfigLoader() {
        try {
            Path path = Paths.get(FILE_PATH);
            if (Files.exists(path)) {
                properties.load(Files.newBufferedReader(path));
            } else {
                logger.log(LoggerLevel.ERROR, "Configuration file not found");
            }
        } catch (IOException e) {
            logger.log(LoggerLevel.ERROR, "Port loading error, default port is used: 5000");
        }
    }

    public int getPort() {
        int port = Integer.parseInt(properties.getProperty("port", "5000"));
        if (port < MIN_PORT || port >= MAX_PORT) {
            throw new NumberFormatException("Invalid port number");
        }
        return Integer.parseInt(properties.getProperty("port", "5000"));
    }

    public String getServerHost() {
        return properties.getProperty("host", "localhost");
    }
}
