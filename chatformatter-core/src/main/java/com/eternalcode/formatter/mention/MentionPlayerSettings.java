package com.eternalcode.formatter.mention;

import com.eternalcode.formatter.mention.service.MentionMetadataService;

import java.util.UUID;

public class MentionPlayerSettings {

    private final MentionConfig config;
    private final MentionMetadataService metadataService;

    public MentionPlayerSettings(MentionConfig config, MentionMetadataService metadataService) {
        this.config = config;
        this.metadataService = metadataService;
    }

    public boolean isMentionSoundEnabled(UUID uuid) {
        return this.metadataService.getMentionSoundEnabled(uuid).orElse(this.config.enabled);
    }

    public void setMentionSoundEnabled(UUID uuid, boolean enabled) {
        this.metadataService.setMentionSoundEnabled(uuid, enabled);
    }

    public boolean toggleMentionSound(UUID uuid) {
        boolean current = this.isMentionSoundEnabled(uuid);
        this.setMentionSoundEnabled(uuid, !current);
        return !current;
    }
}
