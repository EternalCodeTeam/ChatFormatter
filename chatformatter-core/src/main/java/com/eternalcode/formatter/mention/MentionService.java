package com.eternalcode.formatter.mention;

import com.eternalcode.formatter.config.PluginConfig;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MentionService {

    private static final Pattern MENTION_PATTERN = Pattern.compile("([a-zA-Z0-9_]{3,16})");
    private static final Pattern AT_MENTION_PATTERN = Pattern.compile("@([a-zA-Z0-9_]{3,16})");

    private final Server server;
    private final PluginConfig config;
    private final MentionPlayerSettings playerSettings;

    public MentionService(Server server, PluginConfig config, MentionPlayerSettings playerSettings) {
        this.server = server;
        this.config = config;
        this.playerSettings = playerSettings;
    }

    void mentionPlayers(String message) {
        List<Player> mentionedPlayers = this.detectMentions(message);
        Sound sound = loadSound();

        for (Player player : mentionedPlayers) {
            if (this.playerSettings.isMentionSoundEnabled(player.getUniqueId())) {
                player.playSound(player.getLocation(), sound, config.mentions.volume, config.mentions.pitch);
            }
        }
    }

    private List<Player> detectMentions(String message) {
        if (!this.config.mentions.enabled) {
            return List.of();
        }

        List<Player> mentionedPlayers = new ArrayList<>();

        Matcher matcher;
        if (this.config.mentions.requireAtCharacter) {
            matcher = AT_MENTION_PATTERN.matcher(message);
        } else {
            matcher = MENTION_PATTERN.matcher(message);
        }

        while (matcher.find()) {
            String playerName = matcher.group(1);
            Player player;

            if (this.config.mentions.requireFullPlayerName) {
                player = server.getPlayerExact(playerName);
            } else {
                player = server.getPlayer(playerName);
            }

            if (player != null && player.isOnline()) {
                mentionedPlayers.add(player);
            }
        }

        return mentionedPlayers;
    }

    private Sound loadSound() {
        try {
            return Sound.valueOf(config.mentions.sound);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid sound configured for mentions: " + config.mentions.sound);
        }
    }

}
