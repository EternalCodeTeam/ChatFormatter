package com.eternalcode.formatter.legacy;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TextReplacementConfig;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

public final class LegacyProcessor implements UnaryOperator<Component> {

    private final static Replacer REPLACER = new Replacer();

    @Override
    public Component apply(Component component) {
        return component.replaceText(REPLACER);
    }

    private final static class Replacer implements Consumer<TextReplacementConfig.Builder> {

        private final static Pattern ALL = Pattern.compile(".*");
        private final static Replacement REPLACEMENT = new Replacement();

        @Override
        public void accept(TextReplacementConfig.Builder builder) {
            builder
                .match(ALL)
                .replacement(REPLACEMENT);
        }

    }

    private final static class Replacement implements BiFunction<MatchResult, TextComponent.Builder, ComponentLike> {

        @Override
        public ComponentLike apply(MatchResult matchResult, TextComponent.Builder builder) {
            return Legacy.LEGACY_AMPERSAND_SERIALIZER.deserialize(matchResult.group());
        }

    }

}
