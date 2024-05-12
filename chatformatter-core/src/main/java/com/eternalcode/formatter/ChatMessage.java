package com.eternalcode.formatter;

import org.bukkit.entity.Player;

public record ChatMessage(Player sender, Player player, String jsonMessage) {
}
