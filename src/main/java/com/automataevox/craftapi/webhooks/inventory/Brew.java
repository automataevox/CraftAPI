package com.automataevox.craftapi.webhooks.inventory;

import com.automataevox.craftapi.CraftAPI;
import com.automataevox.craftapi.webhooks.RegisterWebHooks;
import com.automataevox.craftapi.webhooks.WebHook;
import com.automataevox.craftapi.webhooks.WebHookEnum;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.BrewEvent;
import org.json.JSONObject;

public final class Brew implements WebHook, Listener {

    private final String eventName = WebHookEnum.BREW.label;

    @Override
    public void register() {
        if (RegisterWebHooks.doActivateWebhook(eventName)) {
            CraftAPI plugin = CraftAPI.getInstance();
            plugin.getServer().getPluginManager().registerEvents(this, plugin);
        }
    }

    @EventHandler
    public void onBrew(final BrewEvent event) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("event", eventName);
        jsonObject.put("fuelLevel", event.getFuelLevel());
        jsonObject.put("location", event.getBlock().getLocation().toString());

        if (event.getContents().getIngredient() != null) {
            jsonObject.put("ingredient", event.getContents().getIngredient().getType().name());
        }
        RegisterWebHooks.sendToAllUrls(jsonObject);
    }
}
