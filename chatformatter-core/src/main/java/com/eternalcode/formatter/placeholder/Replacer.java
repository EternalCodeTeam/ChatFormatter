package com.eternalcode.formatter.placeholder;

import org.bukkit.entity.Player;

public interface Replacer {

    String apply(String text, Player target);
}
