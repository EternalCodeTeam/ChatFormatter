package com.eternalcode.formatter.preparatory;

import org.bukkit.entity.Player;

import java.util.Set;

public interface ChatPreparatory {

    /**
     * Prepare raw json component
     *
     * @param player target player
     * @param receivers receivers of the message
     * @param rawJson raw json component to prepare.
     * @param message original message
     * @param canceled if the message is canceled
     * @return prepared raw json component.
     */
    ChatPrepareResult prepare(Player player, Set<Player> receivers, String rawJson, String message, boolean canceled);

}
