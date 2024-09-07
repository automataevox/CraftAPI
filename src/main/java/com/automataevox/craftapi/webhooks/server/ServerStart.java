package com.automataevox.craftapi.webhooks.server;

import com.automataevox.craftapi.CraftAPI;
import com.automataevox.craftapi.webhooks.RegisterWebHooks;
import com.automataevox.craftapi.webhooks.WebHook;
import com.automataevox.craftapi.webhooks.WebHookEnum;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;
import org.json.JSONObject;

public final class ServerStart implements WebHook, Listener {

    private final String eventName = WebHookEnum.SERVER_START.label;

    @Override
    public void register() {
        if (RegisterWebHooks.doActivateWebhook(eventName)) {
            CraftAPI plugin = CraftAPI.getInstance();
            plugin.getServer().getPluginManager().registerEvents(this, plugin);
        }
    }

    @EventHandler
    public void onServerLoad(final ServerLoadEvent event) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("event", eventName);

        String message;
        if (event.getType() == ServerLoadEvent.LoadType.STARTUP) {
            message = "Server has started";
        } else {
            message = "Server has reloaded";
        }

        jsonObject.put("message", message);
        jsonObject.put("load_type", event.getType().name());

        RegisterWebHooks.sendToAllUrls(jsonObject);
    }
}
