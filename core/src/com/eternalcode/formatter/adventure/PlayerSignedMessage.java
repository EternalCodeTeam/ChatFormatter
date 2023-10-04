package com.eternalcode.formatter.adventure;

import net.kyori.adventure.chat.SignedMessage;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.security.SecureRandom;
import java.time.Instant;

public class PlayerSignedMessage implements SignedMessage {

    private static final SecureRandom RANDOM = new SecureRandom();

    private final Instant instant;
    private final long salt;
    private final Component unsignedContent;
    private final String message;
    private final Identity identity;

    public PlayerSignedMessage(Component unsignedContent, Identity identity) {
        this.identity = identity;
        this.instant = Instant.now();
        this.salt = RANDOM.nextLong();
        this.unsignedContent = unsignedContent;
        this.message = "-";
    }

    @Override
    public @NotNull Instant timestamp() {
        return this.instant;
    }

    @Override
    public long salt() {
        return this.salt;
    }

    @Override
    public Signature signature() {
        return null;
    }

    @Override
    public @Nullable Component unsignedContent() {
        return this.unsignedContent;
    }

    @Override
    public @NotNull String message() {
        return this.message;
    }

    @Override
    public @NotNull Identity identity() {
        return identity;
    }

}
