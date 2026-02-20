package com.eternalcode.formatter.mention.controller;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MentionSuggestionsController implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        this.updateChatSuggestions();
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.updateChatSuggestions();
    }

    private void updateChatSuggestions() {
        Collection<? extends Player> allPlayers = Bukkit.getOnlinePlayers();
        List<String> completions = new ArrayList<>();

        for (Player player : allPlayers) {
            String s = "@" + player.getName();
            completions.add(s);
        }

        for (Player player : allPlayers) {
            player.setCustomChatCompletions(completions);
        }
    }
}
