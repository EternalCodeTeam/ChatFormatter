package pl.eternalmc.chat;

import com.google.common.base.Stopwatch;
import dev.rollczi.litecommands.LiteCommands;
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import org.bukkit.Server;
import org.bukkit.plugin.java.JavaPlugin;
import pl.eternalmc.chat.commands.ReloadCommand;
import pl.eternalmc.chat.config.ConfigManager;
import pl.eternalmc.chat.config.impl.PluginConfig;
import pl.eternalmc.chat.listener.PlayerChatListener;

import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public class ChatPlugin extends JavaPlugin {

    private static ChatPlugin instance;

    private LiteCommands liteCommands;
    private ConfigManager configManager;

    public static ChatPlugin getInstance() {
        return instance;
    }

    public void onEnable() {
        instance = this;

        Stopwatch stopwatch = Stopwatch.createStarted();
        Server server = this.getServer();


        this.configManager = new ConfigManager(this.getDataFolder());
        this.configManager.loadAndRenderConfigs();

        PluginConfig pluginConfig = configManager.getPluginConfig();


        this.liteCommands = LiteBukkitFactory.builder(this.getServer(), "EternalMC-ChatFormatter")
            .typeBind(ConfigManager.class, () -> configManager)
            .typeBind(PluginConfig.class, () -> pluginConfig)
            .typeBind(ChatPlugin.class, () -> this)
            .command(ReloadCommand.class)
            .register();


        Stream.of(
            new PlayerChatListener()
        ).forEach(listener -> this.getServer().getPluginManager().registerEvents(listener, this));


        long millis = stopwatch.elapsed(TimeUnit.MILLISECONDS);
        this.getLogger().info("Plugin enabled in " + millis + "ms");
    }

    public void onDisable() {
        this.liteCommands.getPlatformManager().unregisterCommands();

    }
}
