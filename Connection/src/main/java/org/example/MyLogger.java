package org.example;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MyLogger {
    private static final String LOG_FILE = "file.log";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static MyLogger logger;

    private MyLogger() {
    }

    public static MyLogger getLogger() {
        if (logger == null) {
            synchronized (MyLogger.class) {
                if (logger == null) {
                    logger = new MyLogger();
                }
            }
        }
        return logger;
    }

    public void log(LoggerLevel level, String message) {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        String logMessage = timestamp + " [" + level + "] : " + message;
        System.out.println(logMessage);
        writeToFile(logMessage);
    }

    private void writeToFile(String message) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE, true))) {
            writer.write(message);
            writer.newLine();
        } catch (IOException e) {
            log(LoggerLevel.ERROR, "Error writing to log file: " + e.getMessage());
        }
    }
}
