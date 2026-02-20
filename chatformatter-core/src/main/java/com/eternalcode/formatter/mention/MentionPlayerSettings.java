package com.eternalcode.formatter.mention;

import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

public class MentionPlayerSettings {

    private final Logger logger;
    private final MentionConfig config;
    private final Optional<LuckPermsHook> luckPermsHook;

    public MentionPlayerSettings(Logger logger, MentionConfig config, Optional<LuckPermsHook> luckPermsHook) {
        this.logger = logger;
        this.config = config;
        this.luckPermsHook = luckPermsHook;
    }

    public boolean isMentionSoundEnabled(UUID uuid) {
        return this.luckPermsHook
            .flatMap(hook -> hook.getMentionSoundEnabled(uuid))
            .orElse(this.config.enabled);
    }

    public void setMentionSoundEnabled(UUID uuid, boolean enabled) {
        if (this.luckPermsHook.isEmpty()) {
            this.logger.warning("Cannot set mention sound preference - LuckPerms is not available!");
            return;
        }

        this.luckPermsHook.get().setMentionSoundEnabled(uuid, enabled);
    }

    public boolean toggleMentionSound(UUID uuid) {
        boolean current = this.isMentionSoundEnabled(uuid);
        this.setMentionSoundEnabled(uuid, !current);
        return !current;
    }
}
