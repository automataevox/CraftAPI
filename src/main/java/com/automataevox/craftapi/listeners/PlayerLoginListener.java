package com.automataevox.craftapi.listeners;

import com.automataevox.craftapi.CraftAPI;
import com.automataevox.craftapi.WebSocketServerHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.json.JSONObject;

public class PlayerLoginListener implements Listener {

    @EventHandler
    public void onPlayerLogin(final PlayerLoginEvent event) {
        // Prepare JSON data for WebSocket clients
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("event", "PLAYER_LOGIN");
        jsonObject.put("player", event.getPlayer().getName());
        jsonObject.put("location", event.getPlayer().getLocation().toString());
        jsonObject.put("ip", event.getAddress().getHostAddress());

        // Get the WebSocket server instance from the CraftAPI class
        WebSocketServerHandler wsServer = CraftAPI.getInstance().getWebSocketServer();

        // Broadcast the event to WebSocket clients
        if (wsServer != null) {
            wsServer.broadcast(jsonObject.toString());
        } else {
            System.err.println("WebSocket server is not running.");
        }
    }
}
