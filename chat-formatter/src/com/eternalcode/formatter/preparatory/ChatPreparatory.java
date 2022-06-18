package com.eternalcode.formatter.preparatory;

import org.bukkit.entity.Player;

import java.util.Set;

public interface ChatPreparatory {

    /**
     * Prepare raw json component
     *
     * @param player
     * @param receivers
     * @param rawJson raw json component to prepare.
     * @return prepared raw json component.
     */
    ChatPrepareResult prepare(Player player, Set<Player> receivers, String rawJson, String message, boolean canceled);

}
