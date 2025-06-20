package com.eternalcode.formatter.placeholder;

import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import org.jetbrains.annotations.Nullable;

public class PlaceholderRegistry {

    private final Set<Replacer> replacers = new HashSet<>();
    private final Set<RelationalReplacer> relationalReplacers = new HashSet<>();

    public void addReplacer(Replacer stack) {
        this.replacers.add(stack);
    }

    public void addRelationalReplacer(RelationalReplacer stack) {
        this.relationalReplacers.add(stack);
    }

    public String format(String text, Player target) {
        return this.format(text, target, null);
    }

    public String format(String text, Player target, @Nullable Player viewer) {
        int iterations = 0;
        String beforeReplacements;

        do {
            beforeReplacements = text;

            if (viewer != null) {
                for (RelationalReplacer stack : this.relationalReplacers) {
                    text = stack.apply(text, target, viewer);
                }
            }

            for (Replacer stack : this.replacers) {
                text = stack.apply(text, target);
            }
        } while (!text.equals(beforeReplacements) && iterations++ < 10);

        return text;
    }

}
