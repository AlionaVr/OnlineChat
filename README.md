# Chat Server and Client

## Overview

This project implements a simple client-server chat application using Java.
The server can handle multiple clients concurrently, allowing them to exchange messages in real time.
The application consists of a server, a client, a configuration loader, and a custom logger.

### Server

1. Starts on a specified port.
2. Accepts multiple client connections.
3. Sends messages to all connected clients.
4. Removes clients upon disconnection.

### Client

1. Connects to the server.
2. Sends messages to the server.
3. Receives messages from the server in real-time.
4. Exits the chat using ```/exit``` command.

### Logging

The application logs messages to the console and a file (```file.log```). Logging levels include:

* ```ERROR``` – Logs critical issues.
* ```SERVER_INFO``` – Logs server-related messages.
* ```MESSAGE``` – Logs chat messages.

## Setup the Application

#### Configuration

Modify ```settings.txt``` to set the server port and host:

```
port=5000
host=localhost
```

