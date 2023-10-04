package com.eternalcode.paper.multiversion;

import io.papermc.paper.chat.ChatComposer;
import net.kyori.adventure.text.Component;

public final class LegacyChatComposerProvider implements LegacyDependencyProvider<Object> {

    private final Component component;

    public LegacyChatComposerProvider(Component component) {
        this.component = component;
    }

    @Override
    public Class<?> getType() {
        return ChatComposer.class;
    }

    @Override
    public Object getDependency() {
        return (ChatComposer) (source, sourceDisplayName, ignoredMessage) -> component;
    }

}
