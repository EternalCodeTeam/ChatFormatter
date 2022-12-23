package com.eternalcode.formatter.legacy;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TextReplacementConfig;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import java.util.regex.MatchResult;

public final class LegacyPostMessageProcessor implements UnaryOperator<Component> {

    private static final Replacer REPLACER = new Replacer();

    @Override
    public Component apply(Component component) {
        return component.replaceText(REPLACER);
    }

    private static final class Replacer implements Consumer<TextReplacementConfig.Builder> {

        private static final Replacement REPLACEMENT = new Replacement();

        @Override
        public void accept(TextReplacementConfig.Builder builder) {
            builder
                .match(Legacy.ALL_PATTERN)
                .replacement(REPLACEMENT);
        }

    }

    private static final class Replacement implements BiFunction<MatchResult, TextComponent.Builder, ComponentLike> {

        @Override
        public ComponentLike apply(MatchResult matchResult, TextComponent.Builder builder) {
            return Legacy.LEGACY_AMPERSAND_SERIALIZER.deserialize(matchResult.group());
        }

    }

}
