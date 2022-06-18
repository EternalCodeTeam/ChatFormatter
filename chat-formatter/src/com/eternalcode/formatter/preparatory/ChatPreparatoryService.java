package com.eternalcode.formatter.preparatory;

import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class ChatPreparatoryService {

    private final Set<ChatPreparatory> preparations = new HashSet<>();

    public void registerPreparatory(ChatPreparatory chatPreparatory) {
        this.preparations.add(chatPreparatory);
    }

    public boolean isEmpty() {
        return this.preparations.isEmpty();
    }

    public ChatPrepareResult prepare(Player player, Set<Player> receivers, String json, String message) {
        ChatPrepareResult result = new ChatPrepareResult(json, receivers, false);

        for (ChatPreparatory preparation : preparations) {
            result = preparation.prepare(player, result.getReceivers(), result.getRawMessage(), message, result.isCancelled());
        }

        return result;
    }

}
