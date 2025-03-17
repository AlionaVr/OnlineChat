package org.client;

import org.example.ConfigLoader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    private Socket client;
    private BufferedReader input;
    private PrintWriter output;
    private String username;
    private boolean running = true;

    public void start() {
        ConfigLoader configLoader = new ConfigLoader();
        int port = configLoader.loadPort();
        try {
            client = new Socket("localhost", port);
            output = new PrintWriter(client.getOutputStream(), true);
            input = new BufferedReader(new InputStreamReader(client.getInputStream()));

            BufferedReader scanner = new BufferedReader(new InputStreamReader(System.in));

            System.out.print("Enter your name: ");
            username = scanner.readLine();
            output.println(username);
            System.out.println("Connected to server!");

            Thread receiveThread = new Thread(() -> {
                try {
                    String serverMessage;
                    while (running && (serverMessage = input.readLine()) != null) {
                        System.out.println("Server: " + serverMessage);
                    }
                } catch (IOException e) {
                    if (running) {
                        System.out.println("Connection to server lost: " + e.getMessage());
                    }
                }
            });
            receiveThread.start();

            String message;
            while (running && (message = scanner.readLine()) != null) {
                if (message.equalsIgnoreCase("/exit")) {
                    output.println("/exit");
                    running = false;
                    break;
                }
                output.println(message);
            }
        } catch (IOException e) {
            System.out.println("Client error: " + e.getMessage());
        } finally {
            closeConnection();
        }
    }

    public void closeConnection() {
        running = false;
        try {
            if (client != null) client.close();
            if (input != null) input.close();
            if (output != null) output.close();
            System.out.println("Connection closed.");
        } catch (IOException e) {
            System.out.println("Error closing connection: " + e.getMessage());
        }
    }
}

