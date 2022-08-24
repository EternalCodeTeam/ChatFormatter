package com.eternalcode.formatter.placeholder;

import org.bukkit.entity.Player;

public interface DoublePlayerPlaceholderStack {

    String apply(String text, Player target, Player otherTarget);

}
