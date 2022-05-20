package com.eternalcode.formatter;

import com.eternalcode.formatter.config.ConfigManager;
import com.eternalcode.formatter.config.PluginConfig;
import com.eternalcode.formatter.hook.PlaceholderAPIStack;
import com.eternalcode.formatter.hook.VaultRankProvider;
import com.eternalcode.formatter.legacy.LegacyProcessor;
import com.eternalcode.formatter.placeholder.PlaceholderRegistry;
import com.eternalcode.formatter.template.TemplateService;
import com.google.common.base.Stopwatch;
import dev.rollczi.litecommands.LiteCommands;
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import net.kyori.adventure.platform.AudienceProvider;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public class ChatFormatterPlugin extends JavaPlugin {

    private static ChatFormatterPlugin instance;

    private ConfigManager configManager;

    private PlaceholderRegistry placeholderRegistry;
    private TemplateService templateService;
    private ChatRankProvider rankProvider;

    private AudienceProvider audienceProvider;
    private MiniMessage miniMessage;

    private LiteCommands liteCommands;

    @Override
    public void onEnable() {
        Stopwatch stopwatch = Stopwatch.createStarted();

        instance = this;

        this.configManager = new ConfigManager(this.getDataFolder());
        this.configManager.loadAndRenderConfigs();

        PluginConfig pluginConfig = this.configManager.getPluginConfig();

        this.placeholderRegistry = new PlaceholderRegistry();
        this.placeholderRegistry.stack(pluginConfig);
        this.placeholderRegistry.playerStack(new PlaceholderAPIStack());
        this.templateService = new TemplateService(pluginConfig);
        this.rankProvider = new VaultRankProvider(this.getServer());

        this.audienceProvider = BukkitAudiences.create(this);
        this.miniMessage = MiniMessage.builder()
            .postProcessor(new LegacyProcessor())
            .build();

        this.liteCommands = LiteBukkitFactory.builder(this.getServer(), "chat-formatter")
            .typeBind(PluginConfig.class, () -> pluginConfig)
            .typeBind(ConfigManager.class, () -> this.configManager)
            .typeBind(PlaceholderRegistry.class, () -> this.placeholderRegistry)
            .typeBind(MiniMessage.class, () -> this.miniMessage)
            .typeBind(AudienceProvider.class, () -> this.audienceProvider)

            .command(ChatFormatterCommand.class)
            .register();

        // bStats metrics
        new Metrics(this, 15199);

        Stream.of(
            new ChatController(this.audienceProvider, this.miniMessage, pluginConfig, this.rankProvider, this.placeholderRegistry, templateService)
        ).forEach(listener -> this.getServer().getPluginManager().registerEvents(listener, this));

        this.getLogger().info("Plugin enabled in " + stopwatch.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    @Override
    public void onDisable() {
        this.liteCommands.getPlatformManager().unregisterCommands();
    }

    public PlaceholderRegistry getPlaceholderRegistry() {
        return placeholderRegistry;
    }

    public TemplateService getTemplateService() {
        return templateService;
    }

    public ChatRankProvider getRankProvider() {
        return rankProvider;
    }

}
