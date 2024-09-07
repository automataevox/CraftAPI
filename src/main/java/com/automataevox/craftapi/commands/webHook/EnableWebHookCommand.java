package com.automataevox.craftapi.commands.webHook;

import com.automataevox.craftapi.CraftAPI;
import com.automataevox.craftapi.commands.SubCommand;
import com.automataevox.craftapi.webhooks.WebHookEnum;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public final class EnableWebHookCommand extends SubCommand {
    @Override
    public String getName() {
        return "enable";
    }

    @Override
    public String getDescription() {
        return "Enable a specific webhook.";
    }

    @Override
    public String getSyntax() {
        return "/webhooks enable <webhook>";
    }

    @Override
    public void perform(final CommandSender commandSender, final Command command, final String label, final String[] args) {
        // Check the length of the arguments
        if (args.length != 2) {
            commandSender.sendMessage(ChatColor.RED + "Usage: /webhooks enable <webhook>");
            return;
        }

        // Check if the webhook exists
        String webhookName = args[1];
        if (!WebHookEnum.isValid(webhookName)) {
            commandSender.sendMessage(ChatColor.RED + "Unknown webhook.");
            commandSender.sendMessage(ChatColor.RED + "Available webhooks: " + ChatColor.GREEN + WebHookEnum.getValidHooks());
            return;
        }

        // Enable the webhook
        CraftAPI.getInstance().getConfig().set("webhooks." + webhookName, true);
        CraftAPI.getInstance().saveConfig();
        commandSender.sendMessage(ChatColor.GREEN + "Webhook " + webhookName + " enabled.");
    }

    @Override
    public List<String> getSubcommandArguments(final CommandSender commandSender, final Command command, final String label, final String[] args) {
        if (args.length == 2) {
            return WebHookEnum.getValidHookList();
        }

        return null;
    }
}
