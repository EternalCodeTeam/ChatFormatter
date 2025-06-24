package com.eternalcode.formatter.placeholder;

import org.bukkit.entity.Player;

public interface RelationalReplacer {

    String apply(String text, Player target, Player viewer);
}
