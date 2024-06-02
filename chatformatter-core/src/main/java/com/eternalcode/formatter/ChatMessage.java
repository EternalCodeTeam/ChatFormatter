package com.eternalcode.formatter;

import java.util.Optional;
import org.bukkit.entity.Player;

public record ChatMessage(Player sender, Optional<Player> viewer, String jsonMessage) {
}
