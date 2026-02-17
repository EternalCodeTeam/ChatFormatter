package com.eternalcode.formatter;

import com.eternalcode.formatter.config.ConfigManager;
import com.eternalcode.formatter.config.PluginConfig;
import com.eternalcode.formatter.mention.MentionListener;
import com.eternalcode.formatter.mention.MentionPlayerSettings;
import com.eternalcode.formatter.mention.MentionService;
import com.eternalcode.formatter.mention.controller.MentionSuggestionsController;
import com.eternalcode.formatter.placeholder.ConfiguredReplacer;
import com.eternalcode.formatter.placeholder.PlaceholderRegistry;
import com.eternalcode.formatter.placeholderapi.PlaceholderAPIInitializer;
import com.eternalcode.formatter.rank.ChatRankProvider;
import com.eternalcode.formatter.rank.VaultInitializer;
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

import java.util.concurrent.TimeUnit;

public class ChatFormatterPlugin implements ChatFormatterApi {

    private final PlaceholderRegistry placeholderRegistry;
    private final TemplateService templateService;
    private final ChatRankProvider rankProvider;
    private final ChatHandler chatHandler;
    private final MentionService mentionService;

    public ChatFormatterPlugin(Plugin plugin) {
        Server server = plugin.getServer();
        Stopwatch stopwatch = Stopwatch.createStarted();

        ConfigManager configManager = new ConfigManager(plugin.getDataFolder());
        configManager.loadAndRenderConfigs();

        PluginConfig pluginConfig = configManager.getPluginConfig();

        // PlaceholderAPI support
        this.placeholderRegistry = new PlaceholderRegistry();
        PlaceholderAPIInitializer.initialize(server, this.placeholderRegistry);
        this.placeholderRegistry.addReplacer(new ConfiguredReplacer(pluginConfig));

        this.templateService = new TemplateService(pluginConfig);
        this.rankProvider = VaultInitializer.initialize(server);
        UpdaterService updaterService = new UpdaterService(plugin.getDescription());

        AudienceProvider audienceProvider = BukkitAudiences.create(plugin);
        MiniMessage miniMessage = MiniMessage.miniMessage();
        // bStats metrics
        new Metrics(plugin, 15199);

        this.chatHandler = new ChatHandlerImpl(miniMessage, pluginConfig, this.rankProvider, this.placeholderRegistry, this.templateService);
        MentionPlayerSettings mentionPlayerSettings = new MentionPlayerSettings(server, plugin.getLogger(), configManager.getPluginConfig().mentions);

        server.getPluginCommand("chatformatter").setExecutor(new ChatFormatterCommand(configManager, audienceProvider, miniMessage, mentionPlayerSettings));

        this.mentionService = new MentionService(server, pluginConfig, mentionPlayerSettings);
        server.getPluginManager().registerEvents(new MentionListener(mentionService), plugin);
        server.getPluginManager().registerEvents(new MentionSuggestionsController(), plugin);

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

    @Override
    public MentionService getMentionService() {
        return this.mentionService;
    }

}
