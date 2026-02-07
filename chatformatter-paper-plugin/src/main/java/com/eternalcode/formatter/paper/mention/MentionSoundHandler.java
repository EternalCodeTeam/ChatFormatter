package com.eternalcode.formatter.paper.mention;

import com.eternalcode.formatter.mention.MentionConfig;
import com.eternalcode.formatter.mention.MentionService;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.logging.Logger;

public class MentionSoundHandler {

    private final MentionService mentionService;
    private final Logger logger;

    public MentionSoundHandler(MentionService mentionService, Logger logger) {
        this.mentionService = mentionService;
        this.logger = logger;
    }

    public void playMentionSounds(List<Player> mentionedPlayers) {
        MentionConfig config = this.mentionService.getConfig();

        if (!config.enabled) {
            return;
        }

        Sound sound;
        try {
            sound = Sound.valueOf(config.sound);
        } catch (IllegalArgumentException e) {
            this.logger.warning("Invalid sound configured for mentions: " + config.sound);
            return;
        }

        for (Player player : mentionedPlayers) {
            if (this.mentionService.shouldPlaySound(player)) {
                player.playSound(player.getLocation(), sound, config.volume, config.pitch);
            }
        }
    }
}
