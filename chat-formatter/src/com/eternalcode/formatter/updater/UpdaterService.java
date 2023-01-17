package com.eternalcode.formatter.updater;

import com.eternalcode.formatter.legacy.Legacy;
import com.eternalcode.updater.Updater;
import com.eternalcode.updater.http.RemoteInformation;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
public class UpdaterService {

    private static final String NEW_VERSION_AVAILABLE = "<b><gradient:#29fbff:#38b3ff>ChatFormatter:</gradient></b> <green>New version of ChatFormatter is available, please update!";
    private static final String NO_NEW_VERSION_AVAILABLE = "<b><gradient:#29fbff:#38b3ff>ChatFormatter:</gradient></b> <green>ChatFormatter is up to date!";

    private final MiniMessage miniMessage;

    public UpdaterService(MiniMessage miniMessage) {
        this.miniMessage = miniMessage;
    }

    public void checkForUpdates(Plugin plugin, Player player) {
        Updater updater = new Updater(plugin.getDescription().getName(), plugin.getDescription().getVersion(), "EternalCodeTeam/Updater");
        RemoteInformation remoteInformation = updater.checkUpdates();

        if (remoteInformation.isAvailableNewVersion()) {
            player.sendMessage(Legacy.LEGACY_AMPERSAND_SERIALIZER.serialize(this.miniMessage.deserialize(NO_NEW_VERSION_AVAILABLE)));
        } else {
            player.sendMessage(Legacy.LEGACY_AMPERSAND_SERIALIZER.serialize(this.miniMessage.deserialize(NEW_VERSION_AVAILABLE)));
        }
    }
}
