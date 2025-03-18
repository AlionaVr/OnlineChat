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
    private final int PORT;
    private final String HOST;
    private boolean running = true;

    public Client() {
        ConfigLoader configLoader = new ConfigLoader();
        this.PORT = configLoader.getPort();
        this.HOST = configLoader.getServerHost();
    }

    public void start() {
        try {
            client = new Socket(HOST, PORT);
            output = new PrintWriter(client.getOutputStream(), true);
            input = new BufferedReader(new InputStreamReader(client.getInputStream()));

            System.out.print("Enter your name: ");
            BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in));
            String username = consoleInput.readLine();
            output.println(username);
            System.out.println("Connected to server!");

            Thread receiveThread = new Thread(this::receiveMessages);
            receiveThread.start();

            sendMessages(consoleInput);
        } catch (IOException e) {
            System.out.println("Client error: " + e.getMessage());
        } finally {
            closeConnection();
        }
    }

    private void receiveMessages() {
        try {
            String serverMessage;
            while (running && (serverMessage = input.readLine()) != null) {
                System.out.println(serverMessage);
            }
        } catch (IOException e) {
            System.out.println("Connection to server lost: " + e.getMessage());
        }
    }

    private void sendMessages(BufferedReader consoleInput) throws IOException {
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

