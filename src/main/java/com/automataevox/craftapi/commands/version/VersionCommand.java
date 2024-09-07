package com.automataevox.craftapi.commands.version;

import com.automataevox.craftapi.CraftAPI;
import com.automataevox.craftapi.commands.SubCommand;
import com.automataevox.craftapi.utils.CheckForUpdate;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public final class VersionCommand extends SubCommand {
    @Override
    public String getName() {
        return "version";
    }

    @Override
    public String getDescription() {
        return "Shows the current version of the plugin.";
    }

    @Override
    public String getSyntax() {
        return "/capi version";
    }

    @Override
    public void perform(final CommandSender commandSender, final Command command, final String label, final String[] args) {
        commandSender.sendMessage(ChatColor.GREEN + "CraftAPI version: " + ChatColor.GOLD + CraftAPI.getInstance().getDescription().getVersion());
        commandSender.sendMessage("");
        commandSender.sendMessage(ChatColor.GREEN + "Check for updates...");

        // Check for updates
        CheckForUpdate checkForUpdate = new CheckForUpdate();
        boolean updateAvailable = checkForUpdate.checkForPluginUpdate();
        if (updateAvailable) {
            commandSender.sendMessage(ChatColor.GREEN + "Update available! New Version: "
                    + ChatColor.GOLD + CraftAPI.getInstance().getDescription().getVersion()
                    + ChatColor.GREEN + " -> " + ChatColor.GOLD + checkForUpdate.latestVersion
            );
        } else {
            commandSender.sendMessage(ChatColor.GREEN + "No updates available.");
        }
    }

    @Override
    public List<String> getSubcommandArguments(final CommandSender commandSender, final Command command, final String label, final String[] args) {
        return List.of();
    }
}
