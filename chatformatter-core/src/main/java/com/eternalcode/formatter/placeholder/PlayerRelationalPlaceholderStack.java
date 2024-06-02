package com.eternalcode.formatter.placeholder;

import org.bukkit.entity.Player;

public interface PlayerRelationalPlaceholderStack {

    String apply(String text, Player target, Player viewer);
}
