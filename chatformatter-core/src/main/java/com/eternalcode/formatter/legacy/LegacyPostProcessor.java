package com.eternalcode.formatter.legacy;

import java.util.regex.Pattern;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;

import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public final class LegacyPostProcessor implements UnaryOperator<Component> {

    private static final LegacyComponentSerializer LEGACY_AMPERSAND_SERIALIZER = LegacyComponentSerializer.builder()
        .hexColors()
        .character('&')
        .hexCharacter('#')
        .useUnusualXRepeatedCharacterHexFormat()
        .build();

    private static final Pattern ALL = Pattern.compile(".*");
    private static final Consumer<TextReplacementConfig.Builder> REPLACER = builder -> builder
        .match(ALL)
        .replacement((matchResult, ignored) -> LEGACY_AMPERSAND_SERIALIZER.deserialize(matchResult.group()));

    @Override
    public Component apply(Component component) {
        return component.replaceText(REPLACER);
    }

}
