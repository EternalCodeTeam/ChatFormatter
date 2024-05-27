package com.eternalcode.formatter.placeholder;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PlaceholderRegistry {

    private final Map<String, Placeholder> placeholders = new HashMap<>();
    private final Map<String, PlayerPlaceholder> playerPlaceholders = new HashMap<>();
    private final Map<String, PlayerRelationalPlaceholder> playerRelationalPlaceholders = new HashMap<>();

    private final Set<PlaceholderStack> stacks = new HashSet<>();
    private final Set<PlayerPlaceholderStack> playerStacks = new HashSet<>();
    private final Set<PlayerRelationalPlaceholderStack> playerRelationalStacks = new HashSet<>();

    public void placeholder(String key, Placeholder placeholder) {
        this.placeholders.put(key, placeholder);
    }

    public void playerPlaceholder(String key, PlayerPlaceholder placeholder) {
        this.playerPlaceholders.put(key, placeholder);
    }

    public void playerRelationalPlaceholder(String key, PlayerRelationalPlaceholder placeholder) {
        this.playerRelationalPlaceholders.put(key, placeholder);
    }

    public void stack(PlaceholderStack stack) {
        this.stacks.add(stack);
    }

    public void playerStack(PlayerPlaceholderStack stack) {
        this.playerStacks.add(stack);
    }

    public void playerRelationalStack(PlayerRelationalPlaceholderStack stack) {
        this.playerRelationalStacks.add(stack);
    }

    public String format(String text) {
        for (PlaceholderStack stack : this.stacks) {
            text = stack.apply(text);
        }

        for (Map.Entry<String, Placeholder> entry : this.placeholders.entrySet()) {
            text = text.replace(entry.getKey(), entry.getValue().extract());
        }

        return text;
    }

    public String format(String text, Player target) {
        for (PlayerPlaceholderStack stack : this.playerStacks) {
            text = stack.apply(text, target);
        }

        for (Map.Entry<String, PlayerPlaceholder> entry : this.playerPlaceholders.entrySet()) {
            text = text.replace(entry.getKey(), entry.getValue().extract(target));
        }

        return this.format(text);
    }

    public String format(String text, Player target, Player viewer) {
        for (PlayerRelationalPlaceholderStack stack : this.playerRelationalStacks) {
            text = stack.apply(text, target, viewer);
        }

        for (Map.Entry<String, PlayerRelationalPlaceholder> entry : this.playerRelationalPlaceholders.entrySet()) {
            text = text.replace(entry.getKey(), entry.getValue().extract(target, viewer));
        }

        return this.format(text, target);
    }

}
