package com.eternalcode.formatter.mention;

import org.bukkit.Server;
import org.bukkit.entity.Player;

import java.util.List;

public class MentionService {

    private final MentionDetector detector;
    private final MentionConfig config;

    public MentionService(Server server, MentionConfig config) {
        this.detector = new MentionDetector(server);
        this.config = config;
    }

    public List<Player> detectMentions(String message) {
        if (!this.config.enabled) {
            return List.of();
        }

        return this.detector.detectMentions(message);
    }


    public MentionConfig getConfig() {
        return this.config;
    }
}
