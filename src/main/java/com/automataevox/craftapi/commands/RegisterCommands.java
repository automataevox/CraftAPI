package com.automataevox.craftapi.commands;

import com.automataevox.craftapi.commands.version.VersionCommand;
import com.automataevox.craftapi.commands.webHook.DisableWebHookCommand;
import com.automataevox.craftapi.commands.webHook.EnableWebHookCommand;
import com.automataevox.craftapi.commands.webHook.SendWebHookCommand;
import com.automataevox.craftapi.commands.webHook.ListWebHook;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public final class RegisterCommands {
    private JavaPlugin plugin;

    public RegisterCommands(final JavaPlugin javaPlugin) {
        this.plugin = javaPlugin;
    }

    public void register() {
        List<SubCommand> webHookSubCommands = List.of(
            new ListWebHook(),
            new EnableWebHookCommand(),
            new DisableWebHookCommand(),
            new SendWebHookCommand()
        );
        CommandManager webhookCommandManager = new CommandManager(webHookSubCommands);
        plugin.getCommand("webhooks").setExecutor(webhookCommandManager);


        List<SubCommand> capiSubCommands = List.of(
            new VersionCommand()
        );
        CommandManager versionCommandManager = new CommandManager(capiSubCommands);
        plugin.getCommand("capi").setExecutor(versionCommandManager);
    }
}
