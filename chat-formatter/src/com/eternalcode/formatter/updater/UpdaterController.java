package com.eternalcode.formatter.updater;

import com.eternalcode.formatter.ChatSettings;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.AudienceProvider;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

import java.util.concurrent.CompletableFuture;

public class UpdaterController implements Listener {

    private static final String NEW_VERSION_AVAILABLE = "<b><gradient:#29fbff:#38b3ff>ChatFormatter:</gradient></b> <green>New version of ChatFormatter is available, please update!";

    private final Plugin plugin;
    private final UpdaterService updaterService;
    private final ChatSettings chatSettings;
    private final AudienceProvider audienceProvider;
    private final MiniMessage miniMessage;

    public UpdaterController(Plugin plugin, UpdaterService updaterService, ChatSettings chatSettings, AudienceProvider audienceProvider, MiniMessage miniMessage) {
        this.plugin = plugin;
        this.updaterService = updaterService;
        this.chatSettings = chatSettings;
        this.audienceProvider = audienceProvider;
        this.miniMessage = miniMessage;
    }

    @EventHandler
    void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Audience audience = this.audienceProvider.player(player.getUniqueId());

        if (!player.hasPermission("chatformatter.receiveupdates") || !this.chatSettings.receiveUpdates()) {
            return;
        }

        CompletableFuture<Boolean> upToDate = this.updaterService.isUpToDate(this.plugin);

        upToDate.whenComplete((isUpToDate, throwable) -> {
            if (throwable != null) {
                throwable.printStackTrace();

                return;
            }

            audience.sendMessage(this.miniMessage.deserialize(NEW_VERSION_AVAILABLE));
        });
    }
}
