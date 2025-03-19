package org.client;

import org.example.ConfigLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.*;

class ClientTest {

    private Client client;
    private Socket socketMock;
    private BufferedReader inputMock;
    private PrintWriter outputMock;

    @BeforeEach
    void setUp() throws IOException {
        socketMock = mock(Socket.class);
        inputMock = mock(BufferedReader.class);
        outputMock = mock(PrintWriter.class);
        ConfigLoader configLoaderMock = mock(ConfigLoader.class);

        when(configLoaderMock.getPort()).thenReturn(5000);
        when(configLoaderMock.getServerHost()).thenReturn("localhost");

        when(socketMock.getInputStream()).thenReturn(System.in);
        when(socketMock.getOutputStream()).thenReturn(System.out);

        client = new Client();
        client.clientSocket = socketMock;
        client.input = inputMock;
        client.output = outputMock;
    }

    @Test
    void whenSendMessage_MessageShouldBeSendToServer() throws IOException {
        when(inputMock.readLine()).thenReturn("Hello", null);
        client.sendMessagesToServer(inputMock);

        verify(outputMock, times(1)).println("Hello");
    }

    @Test
    void whenSendMessageExit_thenStopRunning() throws IOException {
        when(inputMock.readLine()).thenReturn("/exit");
        client.sendMessagesToServer(inputMock);

        verify(outputMock, times(1)).println("/exit");

        assertFalse(client.running.get());
    }

    @Test
    void whenCloseConnection_thenCloseAllResources() throws IOException {
        client.closeConnection();

        verify(outputMock, times(1)).close();
        verify(inputMock, times(1)).close();
        verify(socketMock, times(1)).close();
    }
}
