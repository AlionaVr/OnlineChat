package org.client;

import org.example.ConfigLoader;
import org.example.LoggerLevel;
import org.example.MyLogger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

public class Client {
    protected final AtomicBoolean running = new AtomicBoolean(true);
    protected BufferedReader input;
    protected PrintWriter output;
    private final int PORT;
    private final String HOST;
    protected Socket clientSocket;
    private final MyLogger logger = MyLogger.getLogger();

    public Client() {
        ConfigLoader configLoader = new ConfigLoader();
        this.PORT = configLoader.getPort();
        this.HOST = configLoader.getServerHost();
    }

    public void start() {
        try (BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in))) {
            connectToServer();

            output.println(readUsername(consoleInput));

            Thread receiveThread = new Thread(this::receiveMessages);
            receiveThread.start();

            sendMessagesToServer(consoleInput);
        } catch (IOException e) {
            logger.log(LoggerLevel.ERROR, "Client error: " + e.getMessage());
        } finally {
            closeConnection();
        }
    }

    private void connectToServer() throws IOException {
        clientSocket = new Socket(HOST, PORT);
        output = new PrintWriter(clientSocket.getOutputStream(), true);
        input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        logger.log(LoggerLevel.SERVER_INFO, "New client connected to server");
    }

    private String readUsername(BufferedReader consoleInput) throws IOException {
        System.out.print("Enter your name: ");
        return consoleInput.readLine();
    }

    protected void receiveMessages() {
        try {
            String serverMessage;
            while (running.get() && (serverMessage = input.readLine()) != null) {
                System.out.println(serverMessage);
            }
        } catch (IOException e) {
            if (running.get()) {
                logger.log(LoggerLevel.ERROR, "Connection to server lost: " + e.getMessage());
            }
        }
    }

    protected void sendMessagesToServer(BufferedReader consoleInput) throws IOException {
        String message;
        while (running.get() && (message = consoleInput.readLine()) != null) {
            if (message.equalsIgnoreCase("/exit")) {
                output.println("/exit");
                running.set(false);
                break;
            }
            output.println(message);
        }
    }

    public void closeConnection() {
        running.set(false);
        closeResource(clientSocket);
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