package com.eternalcode.formatter.paper.adventure;

import com.eternalcode.paper.multiversion.LegacyDependencyProvider;
import net.kyori.adventure.chat.SignedMessage;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public class PaperSignedMessageProvider implements LegacyDependencyProvider<SignedMessage> {

    private final Component messageComponent;
    private final Player player;

    public PaperSignedMessageProvider(Component messageComponent, Player player) {
        this.messageComponent = messageComponent;
        this.player = player;
    }

    @Override
    public Class<SignedMessage> getType() {
        return SignedMessage.class;
    }

    @Override
    public SignedMessage getDependency() {
        return new PaperSignedMessage(messageComponent, Identity.identity(player.getUniqueId()));
    }

}
