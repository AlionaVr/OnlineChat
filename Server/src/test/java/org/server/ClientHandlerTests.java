package org.server;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
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
        when(mockServer.removeClientFromAllClients(clientHandler)).thenReturn(true);
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
        var order = inOrder(mockSocket, mockInput, mockOutput, mockServer);
        order.verify(mockSocket).close();
        order.verify(mockInput).close();
        order.verify(mockOutput).close();
        order.verify(mockServer).sendMessageToAllClients(anyString());
    }

    @Test
    void whenRegisteringClient_thenStreamsAreReady() throws IOException {
        when(mockSocket.getInputStream()).thenReturn(new ByteArrayInputStream(
                "Alice\n/exit\n".getBytes(StandardCharsets.UTF_8)));
        ByteArrayOutputStream messages = new ByteArrayOutputStream();
        when(mockSocket.getOutputStream()).thenReturn(messages);
        doAnswer(invocation -> {
            assertNotNull(clientHandler.input);
            assertNotNull(clientHandler.output);
            clientHandler.sendMessage("Ready for broadcast");
            return null;
        }).when(mockServer).addClient(clientHandler);

        clientHandler.run();

        assertTrue(messages.toString(StandardCharsets.UTF_8).contains("Ready for broadcast"));
        var order = inOrder(mockServer);
        order.verify(mockServer).addClient(clientHandler);
        order.verify(mockServer).sendMessageToAllClients(contains("Alice connected"));
    }

    @Test
    void whenDisconnectedBeforeUsername_thenNeverRegisteredOrAnnounced() throws IOException {
        when(mockSocket.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[0]));
        when(mockSocket.getOutputStream()).thenReturn(new ByteArrayOutputStream());
        when(mockServer.removeClientFromAllClients(clientHandler)).thenReturn(false);

        clientHandler.run();

        verify(mockServer, never()).addClient(any());
        verify(mockServer, never()).sendMessageToAllClients(anyString());
        verify(mockSocket).close();
    }

    @Test
    void whenDepartureBroadcastFails_thenResourcesStillClosed() throws IOException {
        doThrow(new IllegalStateException("Broadcast failed"))
                .when(mockServer).sendMessageToAllClients(anyString());

        assertDoesNotThrow(clientHandler::closeConnection);

        verify(mockSocket).close();
        verify(mockInput).close();
        verify(mockOutput).close();
    }

    @Test
    void whenInputCloseFails_thenOtherResourcesStillClosed() throws IOException {
        doThrow(new IOException("Read close failed")).when(mockInput).close();

        assertDoesNotThrow(clientHandler::closeConnection);

        verify(mockSocket).close();
        verify(mockOutput).close();
    }

    @Test
    void whenRemovalFails_thenResourcesStillClosed() throws IOException {
        when(mockServer.removeClientFromAllClients(clientHandler))
                .thenThrow(new IllegalStateException("Removal failed"));

        assertDoesNotThrow(clientHandler::closeConnection);

        verify(mockSocket).close();
        verify(mockInput).close();
        verify(mockOutput).close();
    }

    @Test
    void whenClosedTwice_thenResourcesAndDepartureAreHandledOnce() throws IOException {
        clientHandler.closeConnection();
        clientHandler.closeConnection();

        verify(mockServer).removeClientFromAllClients(clientHandler);
        verify(mockServer).sendMessageToAllClients(anyString());
        verify(mockSocket).close();
        verify(mockInput).close();
        verify(mockOutput).close();
    }
}
