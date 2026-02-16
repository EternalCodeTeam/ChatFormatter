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

    private static final Pattern MENTION_PATTERN = Pattern.compile("@([a-zA-Z0-9_]{3,16})");

    private final Server server;
    private final PluginConfig config;

    public MentionService(Server server, PluginConfig config) {
        this.server = server;
        this.config = config;
    }

    void mentionPlayers(String message) {
        List<Player> mentionedPlayers = this.detectMentions(message);
        Sound sound = loadSound();

        for (Player player : mentionedPlayers) {
            player.playSound(player.getLocation(), sound, config.mentions.volume, config.mentions.pitch);
        }
    }

    private List<Player> detectMentions(String message) {
        if (!this.config.mentions.enabled) {
            return List.of();
        }

        List<Player> mentionedPlayers = new ArrayList<>();
        Matcher matcher = MENTION_PATTERN.matcher(message);

        while (matcher.find()) {
            String playerName = matcher.group(1);
            Player player = this.server.getPlayer(playerName);

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
