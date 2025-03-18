package org.example;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MyLoggerTests {

    private static final String LOG_FILE = "file.log";

    @Test
    void whenLog_thenCorrectWriteToFile() throws IOException {
        //arrange
        MyLogger logger = MyLogger.getLogger();
        //act
        logger.log(LoggerLevel.SERVER_INFO, "Test log message");
        List<String> logLines = Files.readAllLines(Paths.get(LOG_FILE));
        //assert
        assertFalse(logLines.isEmpty());
        assertTrue(logLines.getLast().contains("Test log message"));
    }
}
