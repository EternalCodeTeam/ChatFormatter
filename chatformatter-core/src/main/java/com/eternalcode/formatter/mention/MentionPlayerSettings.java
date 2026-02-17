package com.eternalcode.formatter.mention;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.types.MetaNode;
import org.bukkit.Server;

import java.util.UUID;
import java.util.logging.Logger;

public class MentionPlayerSettings {

    private static final String MENTION_SOUND_META_KEY = "chatformatter-mention-sound";

    private final Logger logger;
    private final MentionConfig config;
    private final Server server;
    private LuckPerms luckPerms;

    public MentionPlayerSettings(Server server, Logger logger, MentionConfig config) {
        this.server = server;
        this.logger = logger;
        this.config = config;
        this.initializeLuckPerms();
    }

    private void initializeLuckPerms() {
        if (!this.server.getPluginManager().isPluginEnabled("LuckPerms")) {
            this.logger.warning("LuckPerms is not installed! Mention sound toggle feature will not work.");
            return;
        }

        try {
            this.luckPerms = LuckPermsProvider.get();
        } catch (IllegalStateException e) {
            this.logger.warning("Failed to initialize LuckPerms API: " + e.getMessage());
        }
    }

    public boolean isMentionSoundEnabled(UUID uuid) {
        if (this.luckPerms == null) {
            return this.config.enabled;
        }

        User user = this.luckPerms.getUserManager().getUser(uuid);
        if (user == null) {
            return this.config.enabled;
        }

        String metaValue = user.getCachedData().getMetaData().getMetaValue(MENTION_SOUND_META_KEY);
        if (metaValue == null) {
            return this.config.enabled;
        }

        return Boolean.parseBoolean(metaValue);
    }

    public void setMentionSoundEnabled(UUID uuid, boolean enabled) {
        if (this.luckPerms == null) {
            this.logger.warning("Cannot set mention sound preference - LuckPerms is not available!");
            return;
        }

        this.luckPerms.getUserManager().modifyUser(uuid, user -> {
            user.data().clear(node ->
                node instanceof MetaNode metaNode && metaNode.getMetaKey().equals(MENTION_SOUND_META_KEY)
            );

            MetaNode metaNode = MetaNode.builder(MENTION_SOUND_META_KEY, Boolean.toString(enabled)).build();
            user.data().add(metaNode);
        });
    }

    public boolean toggleMentionSound(UUID uuid) {
        boolean current = this.isMentionSoundEnabled(uuid);
        this.setMentionSoundEnabled(uuid, !current);
        return !current;
    }
}
