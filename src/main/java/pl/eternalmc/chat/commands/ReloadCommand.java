package pl.eternalmc.chat.commands;

import dev.rollczi.litecommands.annotations.Execute;
import dev.rollczi.litecommands.annotations.Permission;
import dev.rollczi.litecommands.annotations.Section;
import org.bukkit.entity.Player;
import pl.eternalmc.chat.config.ConfigManager;
import pl.eternalmc.chat.utils.ChatUtils;

@Section(route = "chatformatter")
@Permission("eternalmc.chat.reload")
public class ReloadCommand {

    private final ConfigManager configManager;

    public ReloadCommand(ConfigManager configManager) {
        this.configManager = configManager;
    }

    @Execute(route = "reload")
    public void reload(Player player) {
        this.configManager.loadAndRenderConfigs();
        player.sendMessage(ChatUtils.color("Successfully reloaded configs!"));
    }
}
