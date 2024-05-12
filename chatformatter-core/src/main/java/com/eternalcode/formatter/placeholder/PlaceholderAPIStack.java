package com.eternalcode.formatter.placeholder;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class PlaceholderAPIStack implements PlayerPlaceholderStack, BiPlayerPlaceholderStack {

    @Override
    public String apply(String text, Player target) {
        return PlaceholderAPI.setPlaceholders(target, text);
    }

    @Override
    public String apply(String text, Player target, @Nullable Player otherTarget) {
        if (otherTarget == null) {
            return PlaceholderAPI.getRelationalPlaceholderPattern().matcher(text).replaceAll("");
        } else {
            return PlaceholderAPI.setRelationalPlaceholders(target, otherTarget, text);
        }
    }

}
