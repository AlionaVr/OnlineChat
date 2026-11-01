package org.example;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class ConfigLoader {
    private static final int MIN_PORT = 1;
    private static final int MAX_PORT = 65535;
    private final MyLogger logger = MyLogger.getLogger();
    private final Properties properties = new Properties();

    public ConfigLoader() {
        this(ConfigLoader.class.getResourceAsStream("/settings.txt"));
    }

    ConfigLoader(InputStream input) {
        if (input == null) {
            logger.log(LoggerLevel.ERROR, "Configuration file not found; using localhost:5000");
            return;
        }
        try (var reader = new InputStreamReader(input, StandardCharsets.UTF_8)) {
            properties.load(reader);
        } catch (IOException e) {
            properties.clear();
            logger.log(LoggerLevel.ERROR, "Configuration loading error; using localhost:5000");
        }
    }

    public int getPort() {
        int port = Integer.parseInt(properties.getProperty("port", "5000").trim());
        if (port < MIN_PORT || port > MAX_PORT) {
            throw new NumberFormatException("Port must be between 1 and 65535");
        }
        return port;
    }

    public String getServerHost() {
        String host = properties.getProperty("host", "localhost").trim();
        if (host.isEmpty()) {
            throw new IllegalArgumentException("Server host must not be blank");
        }
        return host;
    }
}
