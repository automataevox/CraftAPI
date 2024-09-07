package com.automataevox.craftapi;

import com.automataevox.craftapi.commands.RegisterCommands;
import com.automataevox.craftapi.endpoints.RegisterEndpoints;
import com.automataevox.craftapi.utils.CheckForUpdate;
import com.automataevox.craftapi.utils.Logger;
import com.automataevox.craftapi.webhooks.RegisterWebHooks;
import com.automataevox.craftapi.webhooks.server.ServerStop;
import fi.iki.elonen.NanoHTTPD;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.automataevox.craftapi.listeners.PlayerLoginListener;

import java.io.File;

public class CraftAPI extends JavaPlugin  {

    private static final int DEFAULT_PORT = 7000;
    private WebServer server;
    public static FileConfiguration config;
    public static final String pluginName = "CraftAPI";
    private static CraftAPI instance;

    private static boolean blockNewConnections = false;
    private static String blockNewConnectionsMessage;

    public static boolean isPluginInstalled(final String string) {
        return Bukkit.getPluginManager().isPluginEnabled(string);
    }

    @Override
    public final void onEnable() {
        // Check for Updates
        getServer().getPluginManager().registerEvents(new CheckForUpdate(), this);

        registerEvents();
        createConfig();

        config = getConfig();
        instance = this;

        boolean authEnabled = getConfig().getBoolean("authentication.enabled", true);
        String authKey = getConfig().getString("authentication.key", "CHANGE_ME");

        if (!authEnabled) {
            Logger.warning("Authentication is disabled. This is not recommended.");
        } else if ("CHANGE_ME".equals(authKey)) {
            Logger.error("Please change the authKey in the config.yml file.");
        }

        int port = getConfig().getInt("port", DEFAULT_PORT);
        server = new WebServer(port, authEnabled, authKey);

        new RegisterEndpoints(server).registerEndpoints();

        new RegisterWebHooks().registerWebHooks();

        new RegisterCommands(this).register();

        try {
            server.start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
            Logger.info("Web server started on port " + port);
        } catch (Exception e) {
            Logger.error("Failed to start web server: " + e.getMessage());
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public final void onDisable() {
        new ServerStop().register();

        if (server != null) {
            server.stop();
            Logger.info("Web server stopped.");
        }
    }

    private void createConfig() {
        File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists())  {
            saveResource("config.yml", false);
        }
    }

    public static CraftAPI getInstance() {
        return instance;
    }

    private void registerEvents() {
        getServer().getPluginManager().registerEvents(new PlayerLoginListener(), this);
    }

    public static void setBlockNewConnections(final boolean block, final String message) {
        blockNewConnections = block;
        if (block) {
            blockNewConnectionsMessage = message;
        }
    }

    public static boolean isBlockNewConnections() {
        return blockNewConnections;
    }

    public static String getBlockNewConnectionsMessage() {
        return blockNewConnectionsMessage;
    }
}
