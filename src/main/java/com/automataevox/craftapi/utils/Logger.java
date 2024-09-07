package com.automataevox.craftapi.utils;

import com.automataevox.craftapi.CraftAPI;
import org.bukkit.Bukkit;

import java.util.logging.Level;

public final class Logger {

    private Logger() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    private static final String PREFIX = "[CraftAPI] ";

    public static void info(final String message) {
        Bukkit.getLogger().log(java.util.logging.Level.INFO, PREFIX + message);
    }

    public static void warning(final String message) {
        Bukkit.getLogger().log(java.util.logging.Level.WARNING, PREFIX + message);
    }

    public static void error(final String message) {
        Bukkit.getLogger().log(java.util.logging.Level.SEVERE, PREFIX + message);
    }

    public static void debug(final String message) {
        boolean debugMode = CraftAPI.config.getBoolean("debug", false);

        if (debugMode) {
            Bukkit.getLogger().log(Level.INFO, "[DEBUG] " + PREFIX + message);
        }
    }

    public static java.util.logging.Logger getLogger() {
        return Bukkit.getLogger();
    }
}
