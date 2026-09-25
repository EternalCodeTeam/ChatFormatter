package com.eternalcode.formatter.mention.service;

import com.eternalcode.formatter.LuckPermsHook;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.types.MetaNode;
import org.bukkit.Server;

import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

public class MentionMetadataService {

    private static final String MENTION_SOUND_META_KEY = "chatformatter-mention-sound";
    private final LuckPerms luckPerms;

    public MentionMetadataService(Server server, Logger logger) {
        Optional<LuckPerms> luckPermsOptional = LuckPermsHook.initialize(server, logger);
        if (luckPermsOptional.isEmpty()) {
            throw new IllegalStateException("LuckPerms is required for MentionMetadataService but was not found!");
        }
        this.luckPerms = luckPermsOptional.get();
    }

    public Optional<Boolean> getMentionSoundEnabled(UUID uuid) {
        User user = this.luckPerms.getUserManager().getUser(uuid);
        if (user == null) {
            return Optional.empty();
        }

        String metaValue = user.getCachedData().getMetaData().getMetaValue(MENTION_SOUND_META_KEY);
        if (metaValue == null) {
            return Optional.empty();
        }

        return Optional.of(Boolean.parseBoolean(metaValue));
    }

    public void setMentionSoundEnabled(UUID uuid, boolean enabled) {
        this.luckPerms.getUserManager().modifyUser(uuid, user -> {
            user.data().clear(node ->
                    node instanceof MetaNode metaNode && metaNode.getMetaKey().equals(MENTION_SOUND_META_KEY)
            );

            MetaNode metaNode = MetaNode.builder(MENTION_SOUND_META_KEY, Boolean.toString(enabled)).build();
            user.data().add(metaNode);
        });
    }

}
