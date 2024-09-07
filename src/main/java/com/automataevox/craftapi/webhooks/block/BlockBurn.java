package com.automataevox.craftapi.webhooks.block;

import com.automataevox.craftapi.CraftAPI;
import com.automataevox.craftapi.webhooks.RegisterWebHooks;
import com.automataevox.craftapi.webhooks.WebHook;
import com.automataevox.craftapi.webhooks.WebHookEnum;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBurnEvent;
import org.json.JSONObject;

import java.util.Objects;

public final class BlockBurn implements WebHook, Listener {

    private final String eventName = WebHookEnum.BLOCK_BURN.label;

    @Override
    public void register() {
        if (RegisterWebHooks.doActivateWebhook(eventName)) {
            CraftAPI plugin = CraftAPI.getInstance();
            plugin.getServer().getPluginManager().registerEvents(this, plugin);
        }
    }

    @EventHandler
    public void onBlockBurn(final BlockBurnEvent event) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("event", eventName);
        jsonObject.put("block", event.getBlock().getType().name());
        jsonObject.put("location", event.getBlock().getLocation().toString());
        jsonObject.put("ignitingBlock", Objects.requireNonNull(event.getIgnitingBlock()).getType().name());

        RegisterWebHooks.sendToAllUrls(jsonObject);
    }
}
