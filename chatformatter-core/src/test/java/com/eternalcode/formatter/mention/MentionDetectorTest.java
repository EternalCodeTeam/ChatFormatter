package com.eternalcode.formatter.mention;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MentionDetectorTest {

    private Server server;
    private MentionDetector detector;
    private Player mockPlayer;

    @BeforeEach
    void setUp() {
        server = mock(Server.class);
        detector = new MentionDetector(server);
        mockPlayer = mock(Player.class);
    }

    @Test
    void testDetectSingleMention() {
        when(server.getPlayerExact("TestPlayer")).thenReturn(mockPlayer);
        when(mockPlayer.isOnline()).thenReturn(true);

        String message = "Hello @TestPlayer, how are you?";
        List<Player> mentions = detector.detectMentions(message);

        assertEquals(1, mentions.size());
        assertEquals(mockPlayer, mentions.get(0));
    }

    @Test
    void testDetectMultipleMentions() {
        Player mockPlayer2 = mock(Player.class);
        
        when(server.getPlayerExact("Player1")).thenReturn(mockPlayer);
        when(server.getPlayerExact("Player2")).thenReturn(mockPlayer2);
        when(mockPlayer.isOnline()).thenReturn(true);
        when(mockPlayer2.isOnline()).thenReturn(true);

        String message = "Hey @Player1 and @Player2, check this out!";
        List<Player> mentions = detector.detectMentions(message);

        assertEquals(2, mentions.size());
        assertTrue(mentions.contains(mockPlayer));
        assertTrue(mentions.contains(mockPlayer2));
    }

    @Test
    void testDetectNoMentions() {
        String message = "Hello everyone, this is a regular message";
        List<Player> mentions = detector.detectMentions(message);

        assertEquals(0, mentions.size());
    }

    @Test
    void testDetectOfflinePlayer() {
        when(server.getPlayerExact("OfflinePlayer")).thenReturn(null);

        String message = "Hello @OfflinePlayer";
        List<Player> mentions = detector.detectMentions(message);

        assertEquals(0, mentions.size());
    }

    @Test
    void testDetectPlayerNotOnline() {
        when(server.getPlayerExact("TestPlayer")).thenReturn(mockPlayer);
        when(mockPlayer.isOnline()).thenReturn(false);

        String message = "Hello @TestPlayer";
        List<Player> mentions = detector.detectMentions(message);

        assertEquals(0, mentions.size());
    }

    @Test
    void testDetectInvalidUsername() {
        // Usernames that are too short (less than 3 characters)
        String message = "Hello @AB";
        List<Player> mentions = detector.detectMentions(message);

        assertEquals(0, mentions.size());
    }

    @Test
    void testDetectValidUsernameLength() {
        when(server.getPlayerExact("ABC")).thenReturn(mockPlayer);
        when(mockPlayer.isOnline()).thenReturn(true);

        // Minimum valid username length (3 characters)
        String message = "Hello @ABC";
        List<Player> mentions = detector.detectMentions(message);

        assertEquals(1, mentions.size());
    }
}
