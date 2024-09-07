package com.automataevox.craftapi.webhooks;

import com.automataevox.craftapi.CraftAPI;
import com.automataevox.craftapi.utils.Logger;
import com.automataevox.craftapi.webhooks.block.*;
import com.automataevox.craftapi.webhooks.entity.*;
import com.automataevox.craftapi.webhooks.player.*;
import com.automataevox.craftapi.webhooks.block.*;
import com.automataevox.craftapi.webhooks.enchantment.EnchantItem;
import com.automataevox.craftapi.webhooks.entity.*;
import com.automataevox.craftapi.webhooks.inventory.Brew;
import com.automataevox.craftapi.webhooks.inventory.CraftItem;
import com.automataevox.craftapi.webhooks.inventory.FurnaceBurn;
import com.automataevox.craftapi.webhooks.inventory.FurnaceSmelt;
import com.automataevox.craftapi.webhooks.player.*;
import com.automataevox.craftapi.webhooks.server.PluginDisable;
import com.automataevox.craftapi.webhooks.server.PluginEnable;
import com.automataevox.craftapi.webhooks.server.ServerStart;
import com.automataevox.craftapi.webhooks.weather.LightningStrike;
import com.automataevox.craftapi.webhooks.weather.ThunderChange;
import com.automataevox.craftapi.webhooks.weather.WeatherChange;
import com.automataevox.craftapi.webhooks.world.WorldLoad;
import com.automataevox.craftapi.webhooks.world.WorldSave;
import com.automataevox.craftapi.webhooks.world.WorldUnload;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public final class RegisterWebHooks {
    private static List<String> urls;
    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();

    public void registerWebHooks() {

        urls = CraftAPI.config.getStringList("webhooks.urls");

        if (urls.isEmpty()) {
            Logger.warning("No WebHook URL's found in config.yml");
        }

        // Register all webhooks
        new ServerStart().register();
        Logger.debug("Registered server_start WebHook");

        // ServerStop is not registered here, because it is triggered when the plugin gets disabled
        Logger.debug("Registered server_stop WebHook");

        new PluginDisable().register();
        Logger.debug("Registered plugin_disable WebHook");

        new PluginEnable().register();
        Logger.debug("Registered plugin_enable WebHook");

        new BlockBreak().register();
        Logger.debug("Registered block_break WebHook");

        new BlockPlace().register();
        Logger.debug("Registered block_place WebHook");

        new BlockBurn().register();
        Logger.debug("Registered block_burn WebHook");

        new BlockRedstone().register();
        Logger.debug("Registered block_redstone WebHook");

        new NotePlay().register();
        Logger.debug("Registered note_play WebHook");

        new SignChange().register();
        Logger.debug("Registered sign_change WebHook");

        new EnchantItem().register();
        Logger.debug("Registered enchant_item WebHook");

        new CreeperPower().register();
        Logger.debug("Registered creeper_power WebHook");

        new CreatureSpawn().register();
        Logger.debug("Registered creature_spawn WebHook");

        new EntityDeath().register();
        Logger.debug("Registered entity_death WebHook");

        new EntityExplode().register();
        Logger.debug("Registered entity_explode WebHook");

        new EntityShootBow().register();
        Logger.debug("Registered entity_shot_bow WebHook");

        new EntityTame().register();
        Logger.debug("Registered entity_tame WebHook");

        new ExplosionPrime().register();
        Logger.debug("Registered explosion_prime WebHook");

        new PlayerDeath().register();
        Logger.debug("Registered player_death WebHook");

        new Brew().register();
        Logger.debug("Registered brew WebHook");

        new CraftItem().register();
        Logger.debug("Registered craft_item WebHook");

        new FurnaceBurn().register();
        Logger.debug("Registered furnace_burn WebHook");

        new FurnaceSmelt().register();
        Logger.debug("Registered furnace_smelt WebHook");

        new PlayerChat().register();
        Logger.debug("Registered player_chat WebHook");

        new PlayerLogin().register();
        Logger.debug("Registered player_login WebHook");

        new PlayerCommand().register();
        Logger.debug("Registered player_command WebHook");

        new PlayerGamemodeChange().register();
        Logger.debug("Registered player_gamemode_change WebHook");

        new PlayerItemBreak().register();
        Logger.debug("Registered player_item_break WebHook");

        new PlayerKick().register();
        Logger.debug("Registered player_kick WebHook");

        new PlayerQuit().register();
        Logger.debug("Registered player_quit WebHook");

        new PlayerRespawn().register();
        Logger.debug("Registered player_respawn WebHook");

        new LightningStrike().register();
        Logger.debug("Registered lightning_strike WebHook");

        new WeatherChange().register();
        Logger.debug("Registered weather_change WebHook");

        new ThunderChange().register();
        Logger.debug("Registered thunder_change WebHook");

        new WorldLoad().register();
        Logger.debug("Registered world_load WebHook");

        new WorldSave().register();
        Logger.debug("Registered world_save WebHook");

        new WorldUnload().register();
        Logger.debug("Registered world_unload WebHook");
    }

    public static void sendToAllUrls(final JSONObject jsonObject) {
        if (urls == null || urls.isEmpty()) {
            Logger.warning("No WebHook URLs found in config.yml");
            return;
        }

        for (String url : urls) {
            sendWebHook(url, jsonObject);
        }
    }

    private static void sendWebHook(final String url, final JSONObject jsonObject) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonObject.toString()))
                    .build();

            CompletableFuture<HttpResponse<String>> response = HTTP_CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString());

            response.thenAccept(httpResponse -> {
                if (httpResponse.statusCode() == 200) {
                    Logger.debug("WebHook '" + jsonObject.get("event") + "' successfully sent to " + url);
                } else {
                    Logger.warning("Error sending the WebHook '" + jsonObject.get("event") + "' to " + url + ". Response code: " + httpResponse.statusCode());
                }
            }).exceptionally(e -> {
                Logger.error("Error sending the WebHook " + jsonObject.get("event") + " to " + url + ": " + e.getMessage());
                return null;
            });

        } catch (Exception e) {
            Logger.error("Error sending the WebHook " + jsonObject.get("event") + " to " + url + ": " + e.getMessage());
        }
    }

    public static boolean doActivateWebhook(final String eventName) {
        String eventPath = "webhooks." + eventName;

        return CraftAPI.config.getBoolean(eventPath, true);
    }
}
