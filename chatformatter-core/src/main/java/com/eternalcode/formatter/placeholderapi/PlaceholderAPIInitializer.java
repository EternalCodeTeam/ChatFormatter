package com.eternalcode.formatter.placeholderapi;

import com.eternalcode.formatter.placeholder.PlaceholderRegistry;
import org.bukkit.Server;

public class PlaceholderAPIInitializer {

    private PlaceholderAPIInitializer() {
    }

    public static void initialize(Server server, PlaceholderRegistry registry) {
        try {
            Class.forName("me.clip.placeholderapi.PlaceholderAPI");
            PlaceholderAPIReplacer stack = new PlaceholderAPIReplacer();
            registry.addReplacer(stack);
            registry.addRelationalReplacer(stack);
        }
        catch (ClassNotFoundException ignored) {
            server.getLogger().warning("PlaceholderAPI is not installed or not enabled. PlaceholderAPI placeholders will not be available.");
        }
    }


}
