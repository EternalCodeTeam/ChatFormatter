package com.eternalcode.formatter;

import com.eternalcode.formatter.config.ConfigManager;
import com.google.common.base.Stopwatch;
import dev.rollczi.litecommands.command.execute.Execute;
import dev.rollczi.litecommands.command.permission.Permission;
import dev.rollczi.litecommands.command.section.Section;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.AudienceProvider;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;

@Section(route = "chatformatter")
@Permission("chatformatter.chat.reload")
class ChatFormatterCommand {

    private final ConfigManager configManager;
    private final AudienceProvider provider;
    private final MiniMessage miniMessage;

    ChatFormatterCommand(ConfigManager configManager, AudienceProvider provider, MiniMessage miniMessage) {
        this.configManager = configManager;
        this.provider = provider;
        this.miniMessage = miniMessage;
    }

    @Execute(route = "reload")
    void reload(CommandSender commandSender) {
        Stopwatch stopwatch = Stopwatch.createStarted();

        this.configManager.loadAndRenderConfigs();
        long millis = stopwatch.elapsed(TimeUnit.MILLISECONDS);

        Component deserialized = this.miniMessage.deserialize("<b><gradient:#29fbff:#38b3ff>ChatFormatter:</gradient></b> <green>Successfully reloaded configs in " + millis + "ms!");
        Audience audience = commandSender instanceof Player player
            ? this.provider.player(player.getUniqueId())
            : this.provider.console();

        audience.sendMessage(deserialized);
    }

}
