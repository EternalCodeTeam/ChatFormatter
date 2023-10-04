package com.eternalcode.formatter.preparatory;

import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public final class ChatPrepareResult {

    private final Set<Player> receivers = new HashSet<>();
    private final String rawMessage;
    private final boolean cancelled;

    public ChatPrepareResult(String rawMessage, Set<Player> receivers, boolean cancelled) {
        this.rawMessage = rawMessage;
        this.receivers.addAll(receivers);
        this.cancelled = cancelled;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public String getRawMessage() {
        return rawMessage;
    }

    public Set<Player> getReceivers() {
        return receivers;
    }

}
