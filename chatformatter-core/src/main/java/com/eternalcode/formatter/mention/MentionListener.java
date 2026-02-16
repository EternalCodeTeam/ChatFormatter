package com.eternalcode.formatter.mention;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class MentionListener implements Listener {

    private final MentionService mentionService;

    public MentionListener(MentionService mentionService) {
        this.mentionService = mentionService;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    void onMention(AsyncPlayerChatEvent event) {
        this.mentionService.mentionPlayers(event.getMessage());
    }


}
