# Chat Server and Client

## Overview

This project implements a simple client-server chat application using Java.
The server can handle multiple clients concurrently, allowing them to exchange messages in real time.
The application consists of a server, a client, a configuration loader, and a custom logger.

### Server

1. Starts on a specified port.
2. Accepts multiple client connections.
3. Forwards chat messages to all connected clients except the sender.
4. Removes clients upon disconnection.

Connection and disconnection notices are sent to all clients.

### Client

1. Connects to the server.
2. Sends messages to the server.
3. Receives messages from the server in real-time.
4. Exits the chat using ```/exit``` command.

### Logging

The application logs messages to the console and `file.log` in the working directory. Logging levels include:

* ```ERROR``` – Logs errors.
* ```SERVER_INFO``` – Logs server-related messages.
* ```MESSAGE``` – Logs chat messages.

## Setup the Application

#### Configuration

Edit [settings.txt](Connection/src/main/resources/settings.txt) before building:

```properties
port=9999
host=localhost
```

Settings are loaded from the classpath, so rebuild after changing the file.
The port must be between 1 and 65535. If the file cannot be loaded, the application
uses `localhost:5000`.

## Future Improvements

- Add integration tests with multiple clients and unexpected disconnections.
- Support an external configuration file without rebuilding.
- Add a server shutdown command that closes active connections.
- Add private messages and a list of online users.
