package com.automataevox.craftapi.webhooks.block;

import com.automataevox.craftapi.CraftAPI;
import com.automataevox.craftapi.webhooks.RegisterWebHooks;
import com.automataevox.craftapi.webhooks.WebHook;
import com.automataevox.craftapi.webhooks.WebHookEnum;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.NotePlayEvent;
import org.json.JSONObject;

public final class NotePlay implements WebHook, Listener {

    private final String eventName = WebHookEnum.NOTE_PLAY.label;

    @Override
    public void register() {
        if (RegisterWebHooks.doActivateWebhook(eventName)) {
            CraftAPI plugin = CraftAPI.getInstance();
            plugin.getServer().getPluginManager().registerEvents(this, plugin);
        }
    }

    @EventHandler
    public void onNotePlay(final NotePlayEvent event) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("event", eventName);
        jsonObject.put("instrument", event.getInstrument().name());
        jsonObject.put("tone", event.getNote().getTone().name());
        jsonObject.put("octave", event.getNote().getOctave());
        jsonObject.put("pitch", event.getNote().getPitch());
        jsonObject.put("location", event.getBlock().getLocation().toString());

        RegisterWebHooks.sendToAllUrls(jsonObject);
    }
}
