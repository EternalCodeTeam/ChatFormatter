package com.eternalcode.formatter.placeholder;

import org.bukkit.entity.Player;

@FunctionalInterface
public interface BiPlayerPlaceholder {

    String extract(Player target, Player otherTarget);

}
