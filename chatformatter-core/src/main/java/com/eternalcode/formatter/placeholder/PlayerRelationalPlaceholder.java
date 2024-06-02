package com.eternalcode.formatter.placeholder;

import org.bukkit.entity.Player;

@FunctionalInterface
public interface PlayerRelationalPlaceholder {

    String extract(Player target, Player viewer);

}
