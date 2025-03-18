package org.client;

import org.example.ConfigLoader;
import org.example.LoggerLevel;
import org.example.MyLogger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    protected Socket client;
    protected BufferedReader input;
    protected PrintWriter output;
    private final int PORT;
    private final String HOST;
    protected boolean running = true;
    private final MyLogger logger = MyLogger.getLogger();

    public Client() {
        ConfigLoader configLoader = new ConfigLoader();
        this.PORT = configLoader.getPort();
        this.HOST = configLoader.getServerHost();
    }

    public void start() {
        try (BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in))) {
            client = new Socket(HOST, PORT);
            output = new PrintWriter(client.getOutputStream(), true);
            input = new BufferedReader(new InputStreamReader(client.getInputStream()));

            String username = readUsername(consoleInput);
            output.println(username);
            logger.log(LoggerLevel.SERVER_INFO, "Connected to server!");

            Thread receiveThread = new Thread(this::receiveMessages);
            receiveThread.start();

            sendMessages(consoleInput);
        } catch (IOException e) {
            logger.log(LoggerLevel.ERROR, "Client error: " + e.getMessage());
        } finally {
            closeConnection();
        }
    }

    private String readUsername(BufferedReader consoleInput) throws IOException {
        System.out.print("Enter your name: ");
        return consoleInput.readLine();
    }

    protected void receiveMessages() {
        try {
            String serverMessage;
            while (running && (serverMessage = input.readLine()) != null) {
                System.out.println(serverMessage);
            }
        } catch (IOException e) {
            logger.log(LoggerLevel.ERROR, "Connection to server lost: " + e.getMessage());
        }
    }

    protected void sendMessages(BufferedReader consoleInput) throws IOException {
        String message;
        while (running && (message = consoleInput.readLine()) != null) {
            if (message.equalsIgnoreCase("/exit")) {
                output.println("/exit");
                running = false;
                break;
            }
            output.println(message);
        }
    }

    public void closeConnection() {
        running = false;
        closeResource(client);
        closeResource(input);
        closeResource(output);
        logger.log(LoggerLevel.SERVER_INFO, "Connection closed.");
    }

    private void closeResource(AutoCloseable resource) {
        if (resource != null) {
            try {
                resource.close();
            } catch (Exception e) {
                logger.log(LoggerLevel.ERROR, "Error closing resource: " + e.getMessage());
            }
        }
    }
}