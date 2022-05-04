package pl.eternalmc.chat;

import com.google.common.base.Stopwatch;
import dev.rollczi.litecommands.LiteCommands;
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import net.kyori.adventure.platform.AudienceProvider;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.plugin.java.JavaPlugin;
import pl.eternalmc.chat.config.PluginConfig;
import pl.eternalmc.chat.config.ConfigManager;
import pl.eternalmc.chat.hook.PlaceholderAPIStack;
import pl.eternalmc.chat.hook.VaultRankProvider;
import pl.eternalmc.chat.placeholder.PlaceholderRegistry;

import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public class ChatFormatterPlugin extends JavaPlugin {

    private static ChatFormatterPlugin instance;

    private PlaceholderRegistry placeholderRegistry;
    private ChatRankProvider rankProvider;
    private AudienceProvider audienceProvider;
    private MiniMessage miniMessage;

    private ConfigManager configManager;
    private LiteCommands liteCommands;

    @Override
    public void onEnable() {
        Stopwatch stopwatch = Stopwatch.createStarted();

        instance = this;

        this.placeholderRegistry = new PlaceholderRegistry();
        this.placeholderRegistry.playerStack(new PlaceholderAPIStack());
        this.rankProvider = new VaultRankProvider(this.getServer());

        this.audienceProvider = BukkitAudiences.create(this);
        this.miniMessage = MiniMessage.miniMessage();

        this.configManager = new ConfigManager(this.getDataFolder());
        this.configManager.loadAndRenderConfigs();

        PluginConfig pluginConfig = this.configManager.getPluginConfig();

        this.liteCommands = LiteBukkitFactory.builder(this.getServer(), "eternalmc-chatformatter")
            .typeBind(PluginConfig.class, () -> pluginConfig)
            .typeBind(ConfigManager.class, () -> this.configManager)
            .typeBind(PlaceholderRegistry.class, () -> this.placeholderRegistry)

            .command(ChatFormatterCommand.class)
            .register();

        Stream.of(
            new ChatController(this.audienceProvider, this.miniMessage, pluginConfig, this.rankProvider, placeholderRegistry)
        ).forEach(listener -> this.getServer().getPluginManager().registerEvents(listener, this));

        this.getLogger().info("Plugin enabled in " + stopwatch.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    @Override
    public void onDisable() {
        this.liteCommands.getPlatformManager().unregisterCommands();
    }

    public ChatRankProvider getRankProvider() {
        return rankProvider;
    }

    public PlaceholderRegistry getPlaceholderRegistry() {
        return placeholderRegistry;
    }

    public static ChatFormatterPlugin getInstance() {
        return instance;
    }

}
