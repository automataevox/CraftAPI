package com.automataevox.craftapi.webhooks.weather;

import com.automataevox.craftapi.CraftAPI;
import com.automataevox.craftapi.webhooks.RegisterWebHooks;
import com.automataevox.craftapi.webhooks.WebHook;
import com.automataevox.craftapi.webhooks.WebHookEnum;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.LightningStrikeEvent;
import org.json.JSONObject;

public final class LightningStrike implements WebHook, Listener {

    private final String eventName = WebHookEnum.LIGHTNING_STRIKE.label;

    @Override
    public void register() {
        if (RegisterWebHooks.doActivateWebhook(eventName)) {
            CraftAPI plugin = CraftAPI.getInstance();
            plugin.getServer().getPluginManager().registerEvents(this, plugin);
        }
    }

    @EventHandler
    public void onLightningStrike(final LightningStrikeEvent event) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("event", eventName);
        jsonObject.put("location", event.getLightning().getLocation().toString());
        jsonObject.put("cause", event.getCause().name());

        RegisterWebHooks.sendToAllUrls(jsonObject);
    }
}
