package com.eternalcode.formatter.mention;

import com.eternalcode.formatter.config.PluginConfig;
import com.eternalcode.formatter.mention.service.MentionService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class MentionListener implements Listener {

    private final MentionService mentionService;
    private final PluginConfig config;

    public MentionListener(MentionService mentionService, PluginConfig config) {
        this.mentionService = mentionService;
        this.config = config;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    void onMention(AsyncPlayerChatEvent event) {
        if (!this.config.mentions.enabled) {
            return;
        }

        this.mentionService.mentionPlayers(event.getMessage());
    }
}
