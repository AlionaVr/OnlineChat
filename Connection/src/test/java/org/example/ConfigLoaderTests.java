package org.example;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

public class ConfigLoaderTests {
    @Test
    void whenGetPortReturnValidNumber() {
        //arrange
        ConfigLoader configLoader = new ConfigLoader();
        //act
        int port = configLoader.getPort();
        //assert
        assertTrue(port > 0 && port <= 65535, "Port should be a valid number");
    }

    @Test
    void whenGetServerHostShouldNotBeNull() {
        //arrange
        ConfigLoader configLoader = new ConfigLoader();
        //act
        String host = configLoader.getServerHost();
        //assert
        assertNotNull(host, "Host should not be null");
    }

    @Test
    void defaultConstructorReadsBundledSettings() throws IOException {
        Properties expected = new Properties();
        try (InputStream resource = getClass().getResourceAsStream("/settings.txt")) {
            assertNotNull(resource);
            expected.load(resource);
        }
        ConfigLoader config = new ConfigLoader();
        assertEquals(Integer.parseInt(expected.getProperty("port")), config.getPort());
        assertEquals(expected.getProperty("host"), config.getServerHost());
    }

    @Test
    void acceptsMaximumPort() {
        ConfigLoader config = new ConfigLoader(new ByteArrayInputStream(
                "port=65535".getBytes(StandardCharsets.UTF_8)));
        assertEquals(65535, config.getPort());
    }

    @Test
    void rejectsZeroPort() {
        ConfigLoader config = new ConfigLoader(new ByteArrayInputStream(
                "port=0".getBytes(StandardCharsets.UTF_8)));
        assertThrows(NumberFormatException.class, config::getPort);
    }
}

