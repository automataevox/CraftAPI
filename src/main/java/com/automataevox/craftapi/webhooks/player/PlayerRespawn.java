package com.automataevox.craftapi.webhooks.player;

import com.automataevox.craftapi.webhooks.WebHookEnum;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.json.JSONObject;

public final class PlayerRespawn{

    private final String eventName = WebHookEnum.PLAYER_RESPAWN.label;

     @EventHandler
     public void onPlayerRespawn(final PlayerRespawnEvent event) {
         JSONObject jsonObject = new JSONObject();
         jsonObject.put("event", eventName);
         jsonObject.put("player", event.getPlayer().getName());
         jsonObject.put("location", event.getPlayer().getLocation().toString());
         jsonObject.put("respawnLocation", event.getRespawnLocation().toString());
         jsonObject.put("isBedSpawn", event.isBedSpawn());
         jsonObject.put("isAnchorSpawn", event.isAnchorSpawn());
         jsonObject.put("respawnReason", event.getRespawnReason().name());
     }
}
