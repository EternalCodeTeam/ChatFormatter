package com.eternalcode.formatter;

import com.eternalcode.formatter.config.ConfigManager;
import com.eternalcode.formatter.mention.MentionService;
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
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

class ChatFormatterCommand implements CommandExecutor, TabCompleter {

    private static final String RELOAD_MESSAGE = "<b><gradient:#29fbff:#38b3ff>ChatFormatter:</gradient></b> <green>Successfully reloaded configs in %sms!";
    private static final String MENTION_TOGGLE_ENABLED = "<b><gradient:#29fbff:#38b3ff>ChatFormatter:</gradient></b> <green>Mention sounds enabled!";
    private static final String MENTION_TOGGLE_DISABLED = "<b><gradient:#29fbff:#38b3ff>ChatFormatter:</gradient></b> <red>Mention sounds disabled!";
    private static final String PLAYER_ONLY_MESSAGE = "<b><gradient:#29fbff:#38b3ff>ChatFormatter:</gradient></b> <red>This command can only be used by players!";
    
    public static final String RELOAD_PERMISSION = "chatformatter.reload";
    public static final String MENTION_TOGGLE_PERMISSION = "chatformatter.mentiontoggle";

    private final ConfigManager configManager;
    private final AudienceProvider provider;
    private final MiniMessage miniMessage;
    private final MentionService mentionService;

    ChatFormatterCommand(ConfigManager configManager, AudienceProvider provider, MiniMessage miniMessage, MentionService mentionService) {
        this.configManager = configManager;
        this.provider = provider;
        this.miniMessage = miniMessage;
        this.mentionService = mentionService;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(command.getUsage());
            return true;
        }

        String subCommand = args[0].toLowerCase();

        return switch (subCommand) {
            case "reload" -> this.handleReload(sender);
            case "mentiontoggle" -> this.handleMentionToggle(sender);
            default -> {
                sender.sendMessage(command.getUsage());
                yield true;
            }
        };
    }

    private boolean handleReload(CommandSender sender) {
        if (!sender.hasPermission(RELOAD_PERMISSION)) {
            return false;
        }

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

    private boolean handleMentionToggle(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            Component message = this.miniMessage.deserialize(PLAYER_ONLY_MESSAGE);
            Audience audience = this.provider.console();
            audience.sendMessage(message);
            return true;
        }

        if (!player.hasPermission(MENTION_TOGGLE_PERMISSION)) {
            return false;
        }

        this.mentionService.getSettings().toggleMentionSound(player.getUniqueId());
        boolean enabled = this.mentionService.getSettings().isMentionSoundEnabled(player.getUniqueId());

        String messageText = enabled ? MENTION_TOGGLE_ENABLED : MENTION_TOGGLE_DISABLED;
        Component message = this.miniMessage.deserialize(messageText);
        Audience audience = this.provider.player(player.getUniqueId());
        audience.sendMessage(message);

        return true;
    }


    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            
            if (sender.hasPermission(RELOAD_PERMISSION) && "reload".startsWith(args[0].toLowerCase())) {
                completions.add("reload");
            }
            
            if (sender.hasPermission(MENTION_TOGGLE_PERMISSION) && "mentiontoggle".startsWith(args[0].toLowerCase())) {
                completions.add("mentiontoggle");
            }
            
            return completions;
        }

        return Collections.emptyList();
    }
}