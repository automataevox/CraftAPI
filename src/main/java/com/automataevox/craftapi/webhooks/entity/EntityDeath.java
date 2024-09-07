package com.automataevox.craftapi.webhooks.entity;

import com.automataevox.craftapi.CraftAPI;
import com.automataevox.craftapi.webhooks.RegisterWebHooks;
import com.automataevox.craftapi.webhooks.WebHook;
import com.automataevox.craftapi.webhooks.WebHookEnum;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.json.JSONObject;

public final class EntityDeath implements WebHook, Listener {

    private final String eventName = WebHookEnum.ENTITY_DEATH.label;

    @Override
    public void register() {
        if (RegisterWebHooks.doActivateWebhook(eventName)) {
            CraftAPI plugin = CraftAPI.getInstance();
            plugin.getServer().getPluginManager().registerEvents(this, plugin);
        }
    }

    @EventHandler
    public void onEntityDeath(final EntityDeathEvent event) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("event", eventName);
        jsonObject.put("entity", event.getEntity().getType().name());
        jsonObject.put("location", event.getEntity().getLocation().toString());
        jsonObject.put("drops", event.getDrops().toString());
        jsonObject.put("xp", event.getDroppedExp());

        if (event.getEntity().getKiller() != null) {
            jsonObject.put("killedBy", event.getEntity().getKiller().getName());
        } else {
            jsonObject.put("killedBy", "null");
        }

        if (event.getEntity().getLastDamageCause() != null) {
            jsonObject.put("cause", event.getEntity().getLastDamageCause().getCause().name());
        }

        RegisterWebHooks.sendToAllUrls(jsonObject);
    }
}
