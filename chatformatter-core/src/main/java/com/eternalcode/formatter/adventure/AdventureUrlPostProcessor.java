package com.eternalcode.formatter.adventure;

import java.util.function.UnaryOperator;
import java.util.regex.Pattern;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.event.ClickEvent;
import org.jetbrains.annotations.NotNull;

public class AdventureUrlPostProcessor implements UnaryOperator<Component> {

    private static final Pattern URL_PATTERN = Pattern.compile("https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()!@:%_\\+.~#?&\\/\\/=]*)");

    public static final @NotNull TextReplacementConfig CLICKABLE_URL_CONFIG = TextReplacementConfig.builder()
            .match(URL_PATTERN)
            .replacement(url -> url.clickEvent(ClickEvent.openUrl(url.content())))
            .build();

    @Override
    public Component apply(Component component) {
        return component.replaceText(CLICKABLE_URL_CONFIG);
    }
}