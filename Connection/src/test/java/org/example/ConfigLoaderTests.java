package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
}

