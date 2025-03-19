package org.server;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


public class ServerTests {
    private Server server;
    private ClientHandler mockClient1;
    private ClientHandler mockClient2;

    @BeforeEach
    void setUp() {
        server = new Server();
        mockClient1 = mock(ClientHandler.class);
        mockClient2 = mock(ClientHandler.class);
    }

    @Test
    void whenSendMessageToAllClients_thenSendMessageIsCalledForAllClients() {
        Set<ClientHandler> clients = ConcurrentHashMap.newKeySet();
        clients.add(mockClient1);
        clients.add(mockClient2);

        server.getClients().addAll(clients);

        server.sendMessageToAllClients("Test message");
        verify(mockClient1, times(1)).sendMessage("Test message");
        verify(mockClient2, times(1)).sendMessage("Test message");
    }

    @Test
    void whenRemoveClient_thenClientCountIsChanged() {
        server.getClients().add(mockClient1);
        server.getClients().add(mockClient2);

        assertEquals(2, server.getClientCount());

        server.removeClientFromAllClients(mockClient1);

        assertEquals(1, server.getClientCount());
    }
}