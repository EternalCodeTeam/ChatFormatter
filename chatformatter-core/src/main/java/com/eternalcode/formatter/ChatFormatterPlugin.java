package com.eternalcode.formatter;

import com.eternalcode.formatter.config.ConfigManager;
import com.eternalcode.formatter.config.PluginConfig;
import com.eternalcode.formatter.legacy.LegacyPostProcessor;
import com.eternalcode.formatter.legacy.LegacyPreProcessor;
import com.eternalcode.formatter.placeholder.ConfiguredPlaceholderAPIStack;
import com.eternalcode.formatter.placeholder.PlaceholderAPIStack;
import com.eternalcode.formatter.placeholder.PlaceholderRegistry;
import com.eternalcode.formatter.rank.ChatRankProvider;
import com.eternalcode.formatter.rank.VaultRankProvider;
import com.eternalcode.formatter.template.TemplateService;
import com.eternalcode.formatter.updater.UpdaterController;
import com.eternalcode.formatter.updater.UpdaterService;
import com.google.common.base.Stopwatch;
import net.kyori.adventure.platform.AudienceProvider;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bstats.bukkit.Metrics;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.TimeUnit;

public class ChatFormatterPlugin implements ChatFormatterApi {

    private final PlaceholderRegistry placeholderRegistry;
    private final TemplateService templateService;
    private final ChatRankProvider rankProvider;
    private final ChatHandler chatHandler;

    public ChatFormatterPlugin(Plugin plugin) {
        Server server = plugin.getServer();
        Stopwatch stopwatch = Stopwatch.createStarted();

        ConfigManager configManager = new ConfigManager(plugin.getDataFolder());
        configManager.loadAndRenderConfigs();

        PluginConfig pluginConfig = configManager.getPluginConfig();

        this.placeholderRegistry = new PlaceholderRegistry();
        this.placeholderRegistry.playerStack(new ConfiguredPlaceholderAPIStack(pluginConfig));
        this.placeholderRegistry.playerStack(new PlaceholderAPIStack());
        this.templateService = new TemplateService(pluginConfig);
        this.rankProvider = new VaultRankProvider(server);
        UpdaterService updaterService = new UpdaterService(plugin.getDescription());

        AudienceProvider audienceProvider = BukkitAudiences.create(plugin);
        MiniMessage miniMessage = MiniMessage.builder()
            .preProcessor(new LegacyPreProcessor())
            .postProcessor(new LegacyPostProcessor())
            .build();

        // bStats metrics
        new Metrics((JavaPlugin) plugin, 15199);

        this.chatHandler = new ChatHandlerImpl(miniMessage, pluginConfig, this.rankProvider, this.placeholderRegistry, this.templateService);

        server.getPluginCommand("chatformatter").setExecutor(new ChatFormatterCommand(configManager, audienceProvider, miniMessage));
        server.getPluginManager().registerEvents(new UpdaterController(updaterService, pluginConfig, audienceProvider, miniMessage), plugin);

        ChatFormatterApiProvider.enable(this);

        plugin.getLogger().info("Plugin enabled in " + stopwatch.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    public void close() {
        ChatFormatterApiProvider.disable();
    }

    @Override
    public PlaceholderRegistry getPlaceholderRegistry() {
        return this.placeholderRegistry;
    }

    @Override
    public TemplateService getTemplateService() {
        return this.templateService;
    }

    @Override
    public ChatRankProvider getRankProvider() {
        return this.rankProvider;
    }

    @Override
    public ChatHandler getChatHandler() {
        return this.chatHandler;
    }

}
