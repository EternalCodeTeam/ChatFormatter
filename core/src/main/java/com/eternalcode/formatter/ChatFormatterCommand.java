package com.eternalcode.formatter;

import com.eternalcode.formatter.config.PluginConfig;
import com.google.common.base.Stopwatch;
import dev.rollczi.litecommands.command.execute.Execute;
import dev.rollczi.litecommands.command.permission.Permission;
import dev.rollczi.litecommands.command.route.Route;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.AudienceProvider;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;

@Route(name = "chatformatter")
@Permission("chatformatter.chat.reload")
class ChatFormatterCommand {

    private final PluginConfig pluginConfig;
    private final AudienceProvider provider;
    private final MiniMessage miniMessage;

    ChatFormatterCommand(PluginConfig pluginConfig, AudienceProvider provider, MiniMessage miniMessage) {
        this.pluginConfig = pluginConfig;
        this.provider = provider;
        this.miniMessage = miniMessage;
    }

    @Execute(route = "reload")
    void reload(CommandSender commandSender) {
        Stopwatch stopwatch = Stopwatch.createStarted();

        this.pluginConfig.load();
        long millis = stopwatch.elapsed(TimeUnit.MILLISECONDS);

        Component deserialized = this.miniMessage.deserialize("<b><gradient:#29fbff:#38b3ff>ChatFormatter:</gradient></b> <green>Successfully reloaded configs in " + millis + "ms!");
        Audience audience = commandSender instanceof Player player
            ? this.provider.player(player.getUniqueId())
            : this.provider.console();

        audience.sendMessage(deserialized);
    }

}
