package org.server;

import org.example.LoggerLevel;
import org.example.MyLogger;

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
    private final MyLogger logger = MyLogger.getLogger();
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
            server.sendMessageToAllClients("There are " + server.getClientCount() + " clients!", true);

            String clientMessage;
            while ((clientMessage = input.readLine()) != null) {
                if (clientMessage.equalsIgnoreCase("/exit")) {
                    break;
                }

                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                String formattedMessage = "[" + username + "] " + clientMessage;
                server.sendMessageToAllClients(formattedMessage, false);
            }
        } catch (IOException e) {
            logger.log(LoggerLevel.ERROR, " handling client error: " + e.getMessage());
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
            server.sendMessageToAllClients(username + " has left the chat. Now " + server.getClientCount() + " clients.", true);

            if (input != null) input.close();
            if (output != null) output.close();
            if (clientSocket != null && !clientSocket.isClosed()) clientSocket.close();
        } catch (IOException e) {
            logger.log(LoggerLevel.ERROR, "Error closing connection: " + e.getMessage());
        }
    }
}
