package com.eternalcode.formatter.placeholder;

import org.bukkit.entity.Player;

@FunctionalInterface
public interface PlayerPlaceholder {

    String extract(Player target);

}
