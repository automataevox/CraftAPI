package com.automataevox.craftapi.webhooks.block;

import com.automataevox.craftapi.CraftAPI;
import com.automataevox.craftapi.webhooks.RegisterWebHooks;
import com.automataevox.craftapi.webhooks.WebHook;
import com.automataevox.craftapi.webhooks.WebHookEnum;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.json.JSONObject;

public final class BlockPlace implements WebHook, Listener {

    private final String eventName = WebHookEnum.BLOCK_PLACE.label;

    @Override
    public void register() {
        if (RegisterWebHooks.doActivateWebhook(eventName)) {
            CraftAPI plugin = CraftAPI.getInstance();
            plugin.getServer().getPluginManager().registerEvents(this, plugin);
        }
    }

    @EventHandler
    public void onBlockPlace(final BlockPlaceEvent event) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("event", eventName);
        jsonObject.put("player", event.getPlayer().getName());
        jsonObject.put("block", event.getBlock().getType().name());
        jsonObject.put("location", event.getBlock().getLocation().toString());
        jsonObject.put("placedAgainst", event.getBlockAgainst().getType().name());
        jsonObject.put("hand", event.getHand().name());

        RegisterWebHooks.sendToAllUrls(jsonObject);
    }
}
