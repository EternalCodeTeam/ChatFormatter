package com.eternalcode.formatter.updater;

import com.eternalcode.formatter.legacy.Legacy;
import com.eternalcode.gitcheck.GitCheck;
import com.eternalcode.gitcheck.GitCheckResult;
import com.eternalcode.gitcheck.git.GitRepository;
import com.eternalcode.gitcheck.git.GitTag;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
public class UpdaterService {

    private static final String NEW_VERSION_AVAILABLE = "<b><gradient:#29fbff:#38b3ff>ChatFormatter:</gradient></b> <green>New version of ChatFormatter is available, please update!";
    private final MiniMessage miniMessage;

    public UpdaterService(MiniMessage miniMessage) {
        this.miniMessage = miniMessage;
    }

    public void checkForUpdates(Plugin plugin, Player player) {
        GitCheck gitCheck = new GitCheck();
        GitRepository repository = GitRepository.of("EternalCodeTeam", "ChatFormatter");

        GitCheckResult result = gitCheck.checkRelease(repository, GitTag.of(plugin.getDescription().getVersion()));

        if (result.isUpToDate()) {
            Component deserialize = this.miniMessage.deserialize(NEW_VERSION_AVAILABLE);
            player.sendMessage(Legacy.LEGACY_AMPERSAND_SERIALIZER.serialize(deserialize));
        }
    }
}
