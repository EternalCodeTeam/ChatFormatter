package com.eternalcode.formatter.placeholderapi;

import com.eternalcode.formatter.placeholder.Replacer;
import com.eternalcode.formatter.placeholder.RelationalReplacer;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

class PlaceholderAPIReplacer implements Replacer, RelationalReplacer {

    @Override
    public String apply(String text, Player target) {
        return PlaceholderAPI.setPlaceholders(target, text);
    }

    @Override
    public String apply(String text, Player target, Player viewer) {
        return PlaceholderAPI.setRelationalPlaceholders(target, viewer, text);
    }

}
