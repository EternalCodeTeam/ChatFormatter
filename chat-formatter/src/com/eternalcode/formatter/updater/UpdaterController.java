package com.eternalcode.formatter.updater;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

public class UpdaterController implements Listener {

    private final Plugin plugin;
    private final UpdaterService updaterService;

    public UpdaterController(Plugin plugin, UpdaterService updaterService) {
        this.plugin = plugin;
        this.updaterService = updaterService;
    }

    @EventHandler
    void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (player.hasPermission("chatformatter.receiveupdates")) {
            this.updaterService.checkForUpdates(this.plugin, player);
        }
    }
}
