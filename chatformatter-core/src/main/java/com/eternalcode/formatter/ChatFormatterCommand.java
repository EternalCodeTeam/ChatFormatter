package com.eternalcode.formatter;

import com.eternalcode.formatter.config.ConfigManager;
import com.google.common.base.Stopwatch;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.AudienceProvider;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

class ChatFormatterCommand implements CommandExecutor, TabCompleter {

    private static final String RELOAD_MESSAGE = "<b><gradient:#29fbff:#38b3ff>ChatFormatter:</gradient></b> <green>Successfully reloaded configs in %sms!";
    public static final String RELOAD_PERMISSION = "chatformatter.reload";

    private final ConfigManager configManager;
    private final AudienceProvider provider;
    private final MiniMessage miniMessage;

    ChatFormatterCommand(ConfigManager configManager, AudienceProvider provider, MiniMessage miniMessage) {
        this.configManager = configManager;
        this.provider = provider;
        this.miniMessage = miniMessage;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0 || !args[0].equalsIgnoreCase("reload")) {
            sender.sendMessage(command.getUsage());
            return true;
        }

        if (sender.hasPermission(RELOAD_PERMISSION)) {
            Stopwatch stopwatch = Stopwatch.createStarted();

            this.configManager.loadAndRenderConfigs();
            long millis = stopwatch.elapsed(TimeUnit.MILLISECONDS);

            Component deserialized = this.miniMessage.deserialize(String.format(RELOAD_MESSAGE, millis));
            Audience audience = sender instanceof Player player
                ? this.provider.player(player.getUniqueId())
                : this.provider.console();

            audience.sendMessage(deserialized);

            return true;
        }

        return false;
    }


    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1 && sender.hasPermission(RELOAD_PERMISSION)) {
            return List.of("reload");
        }

        return Collections.emptyList();
    }
}