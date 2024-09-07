package com.automataevox.craftapi.webhooks.weather;

import com.automataevox.craftapi.CraftAPI;
import com.automataevox.craftapi.webhooks.RegisterWebHooks;
import com.automataevox.craftapi.webhooks.WebHook;
import com.automataevox.craftapi.webhooks.WebHookEnum;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.json.JSONObject;

public final class ThunderChange implements WebHook, Listener {

    private final String eventName = WebHookEnum.THUNDER_CHANGE.label;

    @Override
    public void register() {
        if (RegisterWebHooks.doActivateWebhook(eventName)) {
            CraftAPI plugin = CraftAPI.getInstance();
            plugin.getServer().getPluginManager().registerEvents(this, plugin);
        }
    }

    @EventHandler
    public void onThunderChange(final ThunderChangeEvent event) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("event", eventName);
        jsonObject.put("world", event.getWorld().getName());
        jsonObject.put("thunder", event.toThunderState());

        RegisterWebHooks.sendToAllUrls(jsonObject);
    }
}
