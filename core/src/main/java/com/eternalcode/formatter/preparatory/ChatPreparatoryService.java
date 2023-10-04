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

    public ChatPrepareResult prepare(Player player, Set<Player> receivers, String jsonFormat, String message) {
        ChatPrepareResult result = new ChatPrepareResult(jsonFormat, receivers, false);

        for (ChatPreparatory preparation : this.preparations) {
            result = preparation.prepare(player, result.getReceivers(), result.getRawMessage(), message, result.isCancelled());
        }

        return result;
    }

}
