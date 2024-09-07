package com.automataevox.craftapi.webhooks.entity;

import com.automataevox.craftapi.CraftAPI;
import com.automataevox.craftapi.webhooks.RegisterWebHooks;
import com.automataevox.craftapi.webhooks.WebHook;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.json.JSONObject;
import com.automataevox.craftapi.webhooks.WebHookEnum;

public final class ExplosionPrime implements WebHook, Listener {

    private final String eventName = WebHookEnum.EXPLOSION_PRIME.label;

    @Override
    public void register() {
        if (RegisterWebHooks.doActivateWebhook(eventName)) {
            CraftAPI plugin = CraftAPI.getInstance();
            plugin.getServer().getPluginManager().registerEvents(this, plugin);
        }
    }

    @EventHandler
    public void onExplosionPrime(final EntityExplodeEvent event) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("event", eventName);
        jsonObject.put("entity", event.getEntity().getType().name());
        jsonObject.put("location", event.getEntity().getLocation().toString());
        jsonObject.put("radius", event.getYield());

        RegisterWebHooks.sendToAllUrls(jsonObject);
    }
}
