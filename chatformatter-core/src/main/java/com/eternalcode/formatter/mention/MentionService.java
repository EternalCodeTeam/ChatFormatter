package com.eternalcode.formatter.mention;

import org.bukkit.Server;
import org.bukkit.entity.Player;

import java.util.List;

public class MentionService {

    private final MentionDetector detector;
    private final MentionSettings settings;
    private final MentionConfig config;

    public MentionService(Server server, MentionConfig config, MentionSettings settings) {
        this.detector = new MentionDetector(server);
        this.settings = settings;
        this.config = config;
    }

    public List<Player> detectMentions(String message, Player sender) {
        if (!this.config.enabled) {
            return List.of();
        }

        List<Player> mentionedPlayers = this.detector.detectMentions(message);
        
        // Remove the sender from mentioned players (can't mention yourself)
        mentionedPlayers.removeIf(player -> player.getUniqueId().equals(sender.getUniqueId()));
        
        return mentionedPlayers;
    }

    public boolean shouldPlaySound(Player player) {
        if (!this.config.enabled) {
            return false;
        }

        return this.settings.isMentionSoundEnabled(player.getUniqueId());
    }

    public MentionSettings getSettings() {
        return this.settings;
    }

    public MentionConfig getConfig() {
        return this.config;
    }
}
