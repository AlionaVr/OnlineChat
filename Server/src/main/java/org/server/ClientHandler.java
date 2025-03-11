package org.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClientHandler implements Runnable {
private static int clientCount = 0;
private Socket clientSocket = null;
private Server server;

private PrintWriter out;
private Scanner in;


    public ClientHandler(Socket socket, Server server) {
        try{
            clientCount++;
            this.server = server;
            this.clientSocket = socket;
            this.out = new PrintWriter(socket.getOutputStream());
            this.in = new Scanner(socket.getInputStream());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }
    public void sendMessage(String message) {
        out.println(message);
        out.flush();
    }
    public void closeConnection() {
        server.removeClientFromAllClients(this);
        clientCount--;
        server.sendMessageToAllClients("clientCount" + clientCount);
    }


    @Override
    public void run() {
        try {
            while (true) {
                server.sendMessageToAllClients("Welcome " + clientCount + "!");
                server.sendMessageToAllClients("now there are " + clientCount + " clients!");
                break;
            }
            while (true) {
                String clientMessage = in.nextLine();
                if (clientMessage.equalsIgnoreCase("exit")) {
                    break;
                }
                System.out.println(clientMessage);
                server.sendMessageToAllClients(clientMessage);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
