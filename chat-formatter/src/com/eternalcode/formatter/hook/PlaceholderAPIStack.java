package com.eternalcode.formatter.hook;

import com.eternalcode.formatter.placeholder.DoublePlayerPlaceholderStack;
import com.eternalcode.formatter.placeholder.PlayerPlaceholderStack;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

public class PlaceholderAPIStack implements PlayerPlaceholderStack, DoublePlayerPlaceholderStack {

    @Override
    public String apply(String text, Player target) {
        return PlaceholderAPI.setPlaceholders(target, text);
    }

    @Override
    public String apply(String text, Player target, Player otherTarget) {
        return PlaceholderAPI.setRelationalPlaceholders(target, otherTarget, text);
    }
}
