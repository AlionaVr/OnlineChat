package org.server;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import static org.mockito.Mockito.*;

public class ClientHandlerTests {
    private ClientHandler clientHandler;
    private Server mockServer;
    private Socket mockSocket;
    private BufferedReader mockInput;
    private PrintWriter mockOutput;

    @BeforeEach
    void setUp() throws IOException {
        mockServer = mock(Server.class);
        mockSocket = mock(Socket.class);
        mockInput = mock(BufferedReader.class);
        mockOutput = mock(PrintWriter.class);

        clientHandler = new ClientHandler(mockSocket, mockServer);

        clientHandler.input = mockInput;
        clientHandler.output = mockOutput;
    }

    @Test
    void whenSendMessage_thenOutputPrintlnCalled() {
        clientHandler.sendMessage("Test message");
        verify(mockOutput, times(1)).println("Test message");
    }

    @Test
    void whenCloseConnection_thenClientRemovedAndResourcesClosed() throws IOException {
        when(mockServer.getClientCount()).thenReturn(0);

        clientHandler.closeConnection();

        verify(mockServer, times(1)).removeClientFromAllClients(clientHandler);
        verify(mockServer, times(1)).sendMessageToAllClients(anyString());
        verify(mockInput, times(1)).close();
        verify(mockOutput, times(1)).close();
        verify(mockSocket, times(1)).close();
    }
}