package com.automataevox.craftapi.webhooks.entity;

import com.automataevox.craftapi.CraftAPI;
import com.automataevox.craftapi.webhooks.RegisterWebHooks;
import com.automataevox.craftapi.webhooks.WebHook;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.json.JSONObject;
import com.automataevox.craftapi.webhooks.WebHookEnum;

public final class EntityShootBow implements WebHook, Listener {

    private final String eventName = WebHookEnum.ENTITY_SHOOT_BOW.label;

    @Override
    public void register() {
        if (RegisterWebHooks.doActivateWebhook(eventName)) {
            CraftAPI plugin = CraftAPI.getInstance();
            plugin.getServer().getPluginManager().registerEvents(this, plugin);
        }
    }

    @EventHandler
    public void onEntityShotBow(final EntityShootBowEvent event) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("event", eventName);
        jsonObject.put("entity", event.getEntity().getType().name());
        jsonObject.put("location", event.getEntity().getLocation().toString());
        jsonObject.put("projectile", event.getProjectile().getType().name());
        jsonObject.put("force", event.getForce());

        RegisterWebHooks.sendToAllUrls(jsonObject);
    }
}
