package com.eternalcode.formatter;

import org.bukkit.entity.Player;

import java.util.List;

public record ChatRenderedMessage(Player sender, String jsonMessage, List<Player> mentionedPlayers) {
    
    public ChatRenderedMessage(Player sender, String jsonMessage) {
        this(sender, jsonMessage, List.of());
    }
}
