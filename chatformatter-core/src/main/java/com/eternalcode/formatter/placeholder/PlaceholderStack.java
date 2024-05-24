package com.eternalcode.formatter.placeholder;

import org.bukkit.entity.Player;

public interface PlaceholderStack {

    String apply(String text, Player target);

}
