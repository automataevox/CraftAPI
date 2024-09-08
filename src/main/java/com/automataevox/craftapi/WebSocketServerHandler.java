package com.automataevox.craftapi;

import com.automataevox.craftapi.listeners.ServerHealthListener;
import com.automataevox.craftapi.utils.Logger;
import org.java_websocket.server.WebSocketServer;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import java.net.InetSocketAddress;
import java.util.*;

public class WebSocketServerHandler extends WebSocketServer {

    public WebSocketServerHandler(int port) {
        super(new InetSocketAddress(port));
        startBroadcastingServerHealth();
    }

    private final Set<WebSocket> clients = Collections.synchronizedSet(new HashSet<>());

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        Logger.info("New connection: " + conn.getRemoteSocketAddress());
        clients.add(conn);
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        Logger.debug("Message from client: " + message);
        conn.send("Echo: " + message);
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        Logger.info("Connection closed: " + conn.getRemoteSocketAddress());
        clients.remove(conn);
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        Logger.error(ex.getMessage());
    }

    @Override
    public void onStart() {
        Logger.info("WebSocket server started successfully");
    }

    @Override
    public void stop() {
        try {
            super.stop();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void startBroadcastingServerHealth() {
        Timer timer = new Timer(true); // Create a new Timer to schedule repeated tasks
        ServerHealthListener serverHealthListener = new ServerHealthListener(); // Create an instance of the listener
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                broadcast(serverHealthListener.getServerHealth().toString()); // Broadcast the health data as a JSON string
            }
        }, 0, 1000); // 0 delay, 1000ms (1s) period
    }

    public void broadcast(String message) {
        synchronized (clients) {
            for (WebSocket client : clients) {
                client.send(message);
            }
        }
    }
}
