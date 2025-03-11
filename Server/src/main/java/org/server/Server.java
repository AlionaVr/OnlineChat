package org.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
    private ArrayList<ClientHandler> clients;
    ConfigLoader configLoader = new ConfigLoader();

    public Server() throws IOException {

        ServerSocket serverSocket = new ServerSocket(configLoader.loadPort("settings.txt"));
        System.out.println("Server started on port " + configLoader.loadPort("settings.txt"));
        Socket clientSocket = null;
        try {
            while (!serverSocket.isClosed()) {
                clientSocket = serverSocket.accept();
                System.out.println("New client connected");
                ClientHandler client = new ClientHandler(clientSocket, this);
                clients.add(client);

                Thread thread = new Thread(client);
                thread.start();

            }
        }
        catch (IOException e) {
        }
        finally {
            clientSocket.close();
            serverSocket.close();
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
