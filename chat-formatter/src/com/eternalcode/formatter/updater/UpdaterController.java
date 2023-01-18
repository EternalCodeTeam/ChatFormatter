package com.eternalcode.formatter.updater;

import com.eternalcode.formatter.ChatSettings;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

public class UpdaterController implements Listener {

    private final Plugin plugin;
    private final UpdaterService updaterService;
    private final ChatSettings chatSettings;

    public UpdaterController(Plugin plugin, UpdaterService updaterService, ChatSettings chatSettings) {
        this.plugin = plugin;
        this.updaterService = updaterService;
        this.chatSettings = chatSettings;
    }

    @EventHandler
    void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (player.hasPermission("chatformatter.receiveupdates") && this.chatSettings.receiveUpdates()) {
            this.updaterService.checkForUpdates(this.plugin, player);
        }
    }
}
