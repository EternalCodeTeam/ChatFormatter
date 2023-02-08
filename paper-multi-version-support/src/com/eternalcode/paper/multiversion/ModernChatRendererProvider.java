package com.eternalcode.paper.multiversion;

import io.papermc.paper.chat.ChatRenderer;
import net.kyori.adventure.text.Component;

public class ModernChatRendererProvider implements LegacyDependencyProvider<Object> {

    private final Component component;

    public ModernChatRendererProvider(Component component) {
        this.component = component;
    }

    @Override
    public Class<?> getType() {
        return ChatRenderer.class;
    }

    @Override
    public Object getDependency() {
        return (ChatRenderer) (source, sourceDisplayName, ignoredMessage, viewer) -> component;
    }

}
