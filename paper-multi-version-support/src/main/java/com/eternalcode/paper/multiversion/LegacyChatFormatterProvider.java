package com.eternalcode.paper.multiversion;

import io.papermc.paper.chat.ChatFormatter;
import net.kyori.adventure.text.Component;

public final class LegacyChatFormatterProvider implements LegacyDependencyProvider<Object> {

    private final Component component;

    public LegacyChatFormatterProvider(Component component) {
        this.component = component;
    }

    @Override
    public Class<?> getType() {
        return ChatFormatter.class;
    }

    @Override
    public Object getDependency() {
        return (ChatFormatter) (displayName, message) -> this.component;
    }

}
