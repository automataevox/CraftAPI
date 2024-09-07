package com.automataevox.craftapi.webhooks.player;

import com.automataevox.craftapi.CraftAPI;
import com.automataevox.craftapi.webhooks.RegisterWebHooks;
import com.automataevox.craftapi.webhooks.WebHook;
import com.automataevox.craftapi.webhooks.WebHookEnum;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.json.JSONObject;

public final class PlayerLogin implements WebHook, Listener {

    private final String eventName = WebHookEnum.PLAYER_LOGIN.label;

    @Override
    public void register() {
        if (RegisterWebHooks.doActivateWebhook(eventName)) {
            CraftAPI plugin = CraftAPI.getInstance();
            plugin.getServer().getPluginManager().registerEvents(this, plugin);
        }
    }

    @EventHandler
    public void onPlayerLogin(final PlayerLoginEvent event) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("event", eventName);
        jsonObject.put("player", event.getPlayer().getName());
        jsonObject.put("location", event.getPlayer().getLocation().toString());
        jsonObject.put("ip", event.getAddress().getHostAddress());

        RegisterWebHooks.sendToAllUrls(jsonObject);
    }

}
