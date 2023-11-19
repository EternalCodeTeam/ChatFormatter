package com.eternalcode.formatter;

import org.bukkit.entity.Player;

public record ChatRenderedMessage(Player sender, String jsonMessage) {
}
