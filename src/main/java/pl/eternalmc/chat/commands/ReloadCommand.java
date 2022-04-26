package pl.eternalmc.chat.commands;

import dev.rollczi.litecommands.annotations.Execute;
import dev.rollczi.litecommands.annotations.Permission;
import dev.rollczi.litecommands.annotations.Section;
import org.bukkit.entity.Player;
import pl.eternalmc.chat.config.ConfigManager;
import pl.eternalmc.chat.config.impl.PluginConfig;

@Section(route = "reload")
@Permission("eternalmc.chat.reload")
public class ReloadCommand {


    private final ConfigManager configManager;
    private final PluginConfig pluginConfig;

    public ReloadCommand(ConfigManager configManager, PluginConfig pluginConfig) {
        this.configManager = configManager;
        this.pluginConfig = pluginConfig;
    }

    @Execute
    public void reload(Player player) {
        this.configManager.render(pluginConfig);

        player.sendMessage("");
    }
}
