package com.eternalcode.formatter.placeholder;

import org.bukkit.entity.Player;

public interface PlayerPlaceholderStack {

    String apply(String text, Player target);

}
