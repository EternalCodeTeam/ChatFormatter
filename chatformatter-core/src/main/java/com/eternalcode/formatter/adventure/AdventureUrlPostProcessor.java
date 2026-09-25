package com.eternalcode.formatter.adventure;

import java.util.function.UnaryOperator;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.event.ClickEvent;
import org.jetbrains.annotations.NotNull;

public class AdventureUrlPostProcessor implements UnaryOperator<Component> {

    public static final @NotNull TextReplacementConfig CLICKABLE_URL_CONFIG = TextReplacementConfig.builder()
            .match(Urls.URL_PATTERN)
            .replacement(url -> url.clickEvent(ClickEvent.openUrl(url.content())))
            .build();

    @Override
    public Component apply(Component component) {
        return component.replaceText(CLICKABLE_URL_CONFIG);
    }
}