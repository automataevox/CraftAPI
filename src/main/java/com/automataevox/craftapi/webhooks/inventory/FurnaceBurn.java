package com.automataevox.craftapi.webhooks.inventory;

import com.automataevox.craftapi.CraftAPI;
import com.automataevox.craftapi.webhooks.RegisterWebHooks;
import com.automataevox.craftapi.webhooks.WebHook;
import com.automataevox.craftapi.webhooks.WebHookEnum;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.json.JSONObject;

public final class FurnaceBurn implements WebHook, Listener {

    private final String eventName = WebHookEnum.FURNACE_BURN.label;

    @Override
    public void register() {
        if (RegisterWebHooks.doActivateWebhook(eventName)) {
            CraftAPI plugin = CraftAPI.getInstance();
            plugin.getServer().getPluginManager().registerEvents(this, plugin);
        }
    }

    @EventHandler
    public void onFurnaceBurn(final FurnaceBurnEvent event) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("event", eventName);
        jsonObject.put("location", event.getBlock().getLocation().toString());
        jsonObject.put("burnTime", event.getBurnTime());
        jsonObject.put("fuel", event.getFuel().getType().name());

        RegisterWebHooks.sendToAllUrls(jsonObject);
    }
}
