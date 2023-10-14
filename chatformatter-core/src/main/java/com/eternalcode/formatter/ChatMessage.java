package com.eternalcode.formatter;

import org.bukkit.entity.Player;

public record ChatMessage(Player sender, String jsonMessage) {

}
