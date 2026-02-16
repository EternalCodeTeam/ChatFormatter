package com.eternalcode.formatter;

import com.eternalcode.formatter.config.ConfigManager;
import com.eternalcode.formatter.mention.MentionPlayerSettings;
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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

class ChatFormatterCommand implements CommandExecutor, TabCompleter {

    private static final String RELOAD_MESSAGE = "<b><gradient:#29fbff:#38b3ff>ChatFormatter:</gradient></b> <green>Successfully reloaded configs in %sms!";
    private static final String PLAYER_ONLY_MESSAGE = "<b><gradient:#29fbff:#38b3ff>ChatFormatter:</gradient></b> <red>Only players can use this command!";
    private static final String TOGGLED_MESSAGE = "<b><gradient:#29fbff:#38b3ff>ChatFormatter:</gradient></b> <green>Toggled mention sound";
    private static final String RELOAD_PERMISSION = "chatformatter.reload";
    private static final String MENTION_TOGGLE_PERMISSION = "chatformatter.mentiontoggle";

    private final ConfigManager configManager;
    private final AudienceProvider provider;
    private final MiniMessage miniMessage;
    private final MentionPlayerSettings playerSettings;


    ChatFormatterCommand(ConfigManager configManager, AudienceProvider provider, MiniMessage miniMessage, MentionPlayerSettings playerSettings) {
        this.configManager = configManager;
        this.provider = provider;
        this.miniMessage = miniMessage;
        this.playerSettings = playerSettings;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(command.getUsage());
            return true;
        }

        Audience audience = sender instanceof Player player
                ? this.provider.player(player.getUniqueId())
                : this.provider.console();

        // /chatformatter reload
        if (args[0].equalsIgnoreCase("reload") && sender.hasPermission(RELOAD_PERMISSION)) {
            Stopwatch stopwatch = Stopwatch.createStarted();

            this.configManager.loadAndRenderConfigs();
            long millis = stopwatch.elapsed(TimeUnit.MILLISECONDS);

            Component deserialized = this.miniMessage.deserialize(String.format(RELOAD_MESSAGE, millis));

            audience.sendMessage(deserialized);

            return true;
        }

        // /chatformatter mentiontoggle
        if (args[0].equalsIgnoreCase("mentiontoggle")) {
            if (!(sender instanceof Player player)) {
                audience.sendMessage(this.miniMessage.deserialize(PLAYER_ONLY_MESSAGE));
                return true;
            }

            if (sender.hasPermission(MENTION_TOGGLE_PERMISSION)) {
                boolean enabled = this.playerSettings.toggleMentionSound(player.getUniqueId());
                audience.sendMessage(this.miniMessage.deserialize(TOGGLED_MESSAGE + (enabled ? " <green>on." : " <red>off.")));
                return true;
            }
        }

        return false;
    }


    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> suggestions = new ArrayList<>();
        if (args.length == 1) {
            if (sender.hasPermission(RELOAD_PERMISSION)) {
                suggestions.add("reload");
            }
            if (sender.hasPermission(MENTION_TOGGLE_PERMISSION)) {
                suggestions.add("mentiontoggle");
            }
        }
        return suggestions;
    }
}