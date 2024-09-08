package com.automataevox.craftapi;

import org.java_websocket.server.WebSocketServer;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class WebSocketServerHandler extends WebSocketServer {

    public WebSocketServerHandler(int port) {
        super(new InetSocketAddress(port));
    }

    private final Set<WebSocket> clients = Collections.synchronizedSet(new HashSet<>());

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println("New connection: " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        System.out.println("Message from client: " + message);
        // Echo message back to the client
        conn.send("Echo: " + message);
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        System.out.println("Connection closed: " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
    }

    @Override
    public void onStart() {
        System.out.println("WebSocket server started successfully");
    }

    @Override
    public void stop() {
        try {
            super.stop();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void broadcast(String message) {
        synchronized (clients) {
            for (WebSocket client : clients) {
                client.send(message);
            }
        }
    }
}
