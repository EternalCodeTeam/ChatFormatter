package com.eternalcode.formatter.updater;

import com.eternalcode.formatter.legacy.Legacy;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class UpdaterService {

    private static final String UPDATE_URL = "https://raw.githubusercontent.com/EternalCodeTeam/ChatFormatter/create-updater/version.txt";

    private final MiniMessage miniMessage;

    public UpdaterService(MiniMessage miniMessage) {
        this.miniMessage = miniMessage;
    }

    public void checkForUpdates(Plugin plugin, Player player) {
        try {
            URL url = new URL(UPDATE_URL);

            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            String versionFromFile = in.readLine();

            if (versionFromFile.equals(plugin.getDescription().getVersion())) {
                player.sendMessage(Legacy.LEGACY_AMPERSAND_SERIALIZER.serialize(this.miniMessage.deserialize("<b><gradient:#29fbff:#38b3ff>ChatFormatter:</gradient></b> <green>ChatFormatter is up to date!")));
            } else {
                player.sendMessage(Legacy.LEGACY_AMPERSAND_SERIALIZER.serialize(this.miniMessage.deserialize("<b><gradient:#29fbff:#38b3ff>ChatFormatter:</gradient></b> <green>New version of ChatFormatter is available, please update!")));
            }
        }
        catch (Exception ignored) {}
    }
}
