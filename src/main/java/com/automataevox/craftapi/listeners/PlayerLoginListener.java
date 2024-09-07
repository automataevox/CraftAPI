package com.automataevox.craftapi.listeners;

import com.automataevox.craftapi.CraftAPI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public final class PlayerLoginListener implements Listener {
    @EventHandler
    public void onPlayerLogin(final PlayerLoginEvent event) {
        if (CraftAPI.isBlockNewConnections()) {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, CraftAPI.getBlockNewConnectionsMessage());
        }
    }
}
