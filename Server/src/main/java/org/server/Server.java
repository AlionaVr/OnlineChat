package org.server;

import org.example.ConfigLoader;
import org.example.LoggerLevel;
import org.example.MyLogger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Server {

    private final Set<ClientHandler> clients;
    private final int PORT;
    private final MyLogger logger;

    public Server() {
        ConfigLoader configLoader = new ConfigLoader();
        this.PORT = configLoader.getPort();
        this.clients = ConcurrentHashMap.newKeySet();
        this.logger = MyLogger.getLogger();
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            logger.log(LoggerLevel.SERVER_INFO, "Server started on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                logger.log(LoggerLevel.SERVER_INFO, "New client connected");

                ClientHandler client = new ClientHandler(clientSocket, this);
                synchronized (clients) {
                    clients.add(client);
                }

                Thread thread = new Thread(client);
                thread.start();
            }
        } catch (IOException e) {
            logger.log(LoggerLevel.ERROR, "Server error: " + e.getMessage());
        }
    }

    public synchronized void sendMessageToAllClients(String message, boolean isServiceMessage) {
        if (isServiceMessage) {
            logger.log(LoggerLevel.SERVER_INFO, message);
        }
        logger.log(LoggerLevel.MESSAGE, message);
        for (ClientHandler client : clients) {
            client.sendMessage(message);
        }
    }

    public synchronized void removeClientFromAllClients(ClientHandler client) {
        clients.remove(client);
    }

    public int getClientCount() {
        return clients.size();
    }

    protected Set<ClientHandler> getClients() {
        return clients;
    }
}