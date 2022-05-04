package pl.eternalmc.chat;

import com.google.common.base.Stopwatch;
import dev.rollczi.litecommands.annotations.Execute;
import dev.rollczi.litecommands.annotations.Permission;
import dev.rollczi.litecommands.annotations.Section;
import net.kyori.adventure.platform.AudienceProvider;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import pl.eternalmc.chat.config.ConfigManager;

import java.util.concurrent.TimeUnit;

@Section(route = "chatformatter")
@Permission("eternalmc.chat.reload")
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
    void reload(Player player) {
        Stopwatch stopwatch = Stopwatch.createStarted();

        this.configManager.loadAndRenderConfigs();
        long millis = stopwatch.elapsed(TimeUnit.MILLISECONDS);
        Component deserialized = this.miniMessage.deserialize("<green>Successfully reloaded configs in " + millis + "ms!");

        this.provider.player(player.getUniqueId()).sendMessage(deserialized);
    }

}
