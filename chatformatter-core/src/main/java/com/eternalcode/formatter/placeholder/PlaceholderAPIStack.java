package com.eternalcode.formatter.placeholder;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

public class PlaceholderAPIStack implements PlayerPlaceholderStack, PlayerRelationalPlaceholderStack {

    @Override
    public String apply(String text, Player target) {
        return PlaceholderAPI.setPlaceholders(target, text);
    }

    @Override
    public String apply(String text, Player target, Player viewer) {
        return PlaceholderAPI.setRelationalPlaceholders(target, viewer, text);
    }

}
