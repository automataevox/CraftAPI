package com.automataevox.craftapi.webhooks.block;

import com.automataevox.craftapi.CraftAPI;
import com.automataevox.craftapi.webhooks.RegisterWebHooks;
import com.automataevox.craftapi.webhooks.WebHook;
import com.automataevox.craftapi.webhooks.WebHookEnum;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.json.JSONObject;

public final class BlockBreak implements WebHook, Listener {

    private final String eventName = WebHookEnum.BLOCK_BREAK.label;

    @Override
    public void register() {
        if (RegisterWebHooks.doActivateWebhook(eventName)) {
            CraftAPI plugin = CraftAPI.getInstance();
            plugin.getServer().getPluginManager().registerEvents(this, plugin);
        }
    }

    @EventHandler
    public void onBlockBreak(final BlockBreakEvent event) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("event", eventName);
        jsonObject.put("player", event.getPlayer().getName());
        jsonObject.put("block", event.getBlock().getType().name());
        jsonObject.put("location", event.getBlock().getLocation().toString());

        RegisterWebHooks.sendToAllUrls(jsonObject);
    }
}
