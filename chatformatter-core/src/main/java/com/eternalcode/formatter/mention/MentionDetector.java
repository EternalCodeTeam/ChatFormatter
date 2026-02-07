package com.eternalcode.formatter.mention;

import org.bukkit.Server;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MentionDetector {

    private static final Pattern MENTION_PATTERN = Pattern.compile("@([a-zA-Z0-9_]{3,16})");

    private final Server server;

    public MentionDetector(Server server) {
        this.server = server;
    }

    public List<Player> detectMentions(String message) {
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
}
