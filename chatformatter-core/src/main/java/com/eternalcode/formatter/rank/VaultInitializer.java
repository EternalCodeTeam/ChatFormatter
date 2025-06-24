package com.eternalcode.formatter.rank;

import org.bukkit.Server;

public final class VaultInitializer {

    private VaultInitializer() {
    }

    public static ChatRankProvider initialize(Server server) {
        try {
            Class.forName("net.milkbowl.vault.permission.Permission");
            return new VaultRankProvider(server);
        }
        catch (ClassNotFoundException e) {
            server.getLogger().warning("Vault is not installed or not enabled. Some features may not work. You can use only 'default' and 'op' ranks.");
            return new EmptyRankProvider();
        }
    }

}
