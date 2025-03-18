package org.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private final Server server;
    private String username = "Anonymous";

    private PrintWriter output;
    private BufferedReader input;

    public ClientHandler(Socket socket, Server server) {
        this.clientSocket = socket;
        this.server = server;
    }

    @Override
    public void run() {
        try {
            input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            output = new PrintWriter(clientSocket.getOutputStream(), true);

            String clientName = input.readLine();
            if (clientName != null && !clientName.trim().isEmpty()) {
                this.username = clientName;
            }
            server.sendMessageToAllClients("Welcome " + username + "!", true);
            server.sendMessageToAllClients("Now there are " + server.getClients().size() + " clients!", true);

            String clientMessage;
            while ((clientMessage = input.readLine()) != null) {
                if (clientMessage.equalsIgnoreCase("/exit")) {
                    break;
                }

                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                String formattedMessage = "[" + timestamp + "] " + username + ": " + clientMessage;

                System.out.println(formattedMessage);
                server.sendMessageToAllClients(formattedMessage, false);
            }
        } catch (IOException e) {
            System.out.println("Error handling client: " + e.getMessage());
        } finally {
            closeConnection();
        }
    }

    public void sendMessage(String message) {
        output.println(message);
    }

    public void closeConnection() {
        try {
            server.removeClientFromAllClients(this);
            server.sendMessageToAllClients(username + " has left the chat. Now " + server.getClients().size() + " clients.", true);

            if (input != null) input.close();
            if (output != null) output.close();
            if (clientSocket != null && !clientSocket.isClosed()) clientSocket.close();
        } catch (IOException e) {
            System.out.println("Error closing connection: " + e.getMessage());
        }
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
