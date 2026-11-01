package org.server;

import org.example.LoggerLevel;
import org.example.MyLogger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private final Server server;
    private String username = "Anonymous";
    private final MyLogger logger = MyLogger.getLogger();
    protected PrintWriter output;
    protected BufferedReader input;
    private volatile boolean closed;

    public ClientHandler(Socket socket, Server server) {
        this.clientSocket = socket;
        this.server = server;
    }

    @Override
    public void run() {
        if (closed) {
            return;
        }
        try {
            input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            output = new PrintWriter(clientSocket.getOutputStream(), true);

            if (!checkUsername()) {
                return;
            }
            processClientMessages();

        } catch (IOException e) {
            logger.log(LoggerLevel.ERROR, " handling client error: " + e.getMessage());
        } finally {
            closeConnection();
        }
    }

    private boolean checkUsername() throws IOException {
        String clientName = input.readLine();
        if (clientName == null) {
            return false;
        }
        if (!clientName.trim().isEmpty()) {
            this.username = clientName;
        }
        if (!registerClient()) {
            return false;
        }
        String joinMessage = username + " connected to server! (count: " + server.getClientCount() + ")";

        server.sendMessageToAllClients(joinMessage);
        return true;
    }

    private synchronized boolean registerClient() {
        if (closed) {
            return false;
        }
        // Publish the handler only after its streams and username are ready.
        server.addClient(this);
        return true;
    }

    private void processClientMessages() throws IOException {
        String clientMessage;
        while (!closed && (clientMessage = input.readLine()) != null) {
            if (clientMessage.equalsIgnoreCase("/exit")) {
                break;
            }

            String formattedMessage = " [" + username + "] " + clientMessage;
            server.sendMessageToAllExceptSender(this, formattedMessage);
        }
    }

    public void sendMessage(String message) {
        if (!closed) {
            output.println(message);
        }
    }

    public synchronized void closeConnection() {
        if (closed) {
            return;
        }
        closed = true;
        // Release I/O before registry calls or notifications, unblocking readers and writers.
        closeResource(clientSocket);
        closeResource(input);
        closeResource(output);
        boolean removed = false;
        try {
            removed = server.removeClientFromAllClients(this);
        } catch (RuntimeException e) {
            logger.log(LoggerLevel.ERROR, "Error removing client: " + e.getMessage());
        }
        if (removed) {
            try {
                server.sendMessageToAllClients(username + " has left the chat. Now " + server.getClientCount() + " clients.");
            } catch (RuntimeException e) {
                logger.log(LoggerLevel.ERROR, "Error announcing client departure: " + e.getMessage());
            }
        }
    }

    private void closeResource(AutoCloseable resource) {
        if (resource != null) {
            try {
                resource.close();
            } catch (Exception e) {
                logger.log(LoggerLevel.ERROR, "Error closing connection: " + e.getMessage());
            }
        }
    }
}
