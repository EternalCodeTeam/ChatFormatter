package com.eternalcode.formatter.mention.controller;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MentionSuggestionsController implements Listener {

    private final Server server;

    public MentionSuggestionsController(Server server) {
        this.server = server;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        this.updateChatSuggestions();
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.updateChatSuggestions();
    }

    private void updateChatSuggestions() {
        Collection<? extends Player> allPlayers = this.server.getOnlinePlayers();
        List<String> completions = new ArrayList<>();

        for (Player player : allPlayers) {
            if (player.isInvisible()) { // Vanish compatibility
                continue;
            }

            String mention = "@" + player.getName();
            completions.add(mention);
        }

        for (Player player : allPlayers) {
            player.setCustomChatCompletions(completions);
        }
    }
}
