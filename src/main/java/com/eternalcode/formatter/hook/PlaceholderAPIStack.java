package com.eternalcode.formatter.hook;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import com.eternalcode.formatter.placeholder.PlayerPlaceholderStack;

public class PlaceholderAPIStack implements PlayerPlaceholderStack {

    @Override
    public String apply(String text, Player target) {
        return PlaceholderAPI.setPlaceholders(target, text);
    }

}
