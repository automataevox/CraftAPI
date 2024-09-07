package com.automataevox.craftapi.webhooks.weather;

import com.automataevox.craftapi.CraftAPI;
import com.automataevox.craftapi.webhooks.RegisterWebHooks;
import com.automataevox.craftapi.webhooks.WebHook;
import com.automataevox.craftapi.webhooks.WebHookEnum;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.json.JSONObject;

public final class WeatherChange implements WebHook, Listener {

    private final String eventName = WebHookEnum.WEATHER_CHANGE.label;

    @Override
    public void register() {
        if (RegisterWebHooks.doActivateWebhook(eventName)) {
            CraftAPI plugin = CraftAPI.getInstance();
            plugin.getServer().getPluginManager().registerEvents(this, plugin);
        }
    }

    @EventHandler
    public void onWeatherChange(final WeatherChangeEvent event) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("event", eventName);
        jsonObject.put("world", event.getWorld().getName());
        jsonObject.put("rain", event.toWeatherState());

        RegisterWebHooks.sendToAllUrls(jsonObject);
    }
}
