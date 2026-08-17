package com.eternalcode.formatter;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import org.bukkit.Server;

import java.util.Optional;
import java.util.logging.Logger;

public class LuckPermsHook {

    public static Optional<LuckPerms> initialize(Server server, Logger logger) {
        if (!server.getPluginManager().isPluginEnabled("LuckPerms")) {
            return Optional.empty();
        }

        try {
            return Optional.of(LuckPermsProvider.get());
        } catch (IllegalStateException e) {
            logger.warning("Failed to initialize LuckPerms API: " + e.getMessage());
            return Optional.empty();
        }
    }
}

