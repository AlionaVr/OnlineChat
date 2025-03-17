package org.server;

import org.example.ConfigLoader;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    private final Set<ClientHandler> clients = ConcurrentHashMap.newKeySet();
    private ConfigLoader configLoader = new ConfigLoader();
    private final int PORT = configLoader.loadPort();

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected");

                ClientHandler client = new ClientHandler(clientSocket, this);
                clients.add(client);

                Thread thread = new Thread(client);
                thread.start();
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }

    public void sendMessageToAllClients(String message) {
        for (ClientHandler client : clients) {
            client.sendMessage(message);
        }
    }

    public void removeClientFromAllClients(ClientHandler client) {
        clients.remove(client);
    }
}
