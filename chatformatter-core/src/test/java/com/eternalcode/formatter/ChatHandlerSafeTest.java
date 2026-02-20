package com.eternalcode.formatter;

import com.eternalcode.formatter.config.PluginConfig;
import com.eternalcode.formatter.placeholder.PlaceholderRegistry;
import com.eternalcode.formatter.rank.ChatRankProvider;
import com.eternalcode.formatter.template.TemplateService;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ChatHandlerSafeTest {

    private PluginConfig config;
    private ChatHandlerImpl chatHandler;
    private Player player;

    @BeforeEach
    void setup() {
        config = new PluginConfig();
        ChatRankProvider rankProvider = mock(ChatRankProvider.class);
        PlaceholderRegistry placeholderRegistry = mock(PlaceholderRegistry.class);
        TemplateService templateService = mock(TemplateService.class);

        when(rankProvider.getRank(any())).thenReturn("default");
        when(placeholderRegistry.format(anyString(), any())).thenAnswer(inv -> {
            String s = inv.getArgument(0);
            return s.replace("{message}", "<message>")
                    .replace("{displayname}", "Player")
                    .replace("{member}", "")
                    .replace("$hoverName(Player)", "");
        });
        when(templateService.applyTemplates(anyString())).thenAnswer(inv -> inv.getArgument(0));

        MiniMessage miniMessage = MiniMessage.miniMessage();

        chatHandler = new ChatHandlerImpl(miniMessage, config, rankProvider, placeholderRegistry, templateService);
        player = mock(Player.class);
    }

    @Test
    void testUnsafeTagsWithoutPermission() {
        when(player.hasPermission("chatformatter.click")).thenReturn(true);
        when(player.hasPermission("chatformatter.unsafe")).thenReturn(false);
        when(player.getDisplayName()).thenReturn("Player");
        when(player.getName()).thenReturn("Player");

        String rawMessage = "<click:run_command:'/op me'>Click me</click>";
        String jsonMessage = GsonComponentSerializer.gson().serialize(Component.text(rawMessage));

        ChatMessage chatMessage = new ChatMessage(player, Optional.empty(), jsonMessage);

        ChatRenderedMessage result = chatHandler.process(chatMessage);
        String json = result.jsonMessage();

        assertFalse(json.contains("\"clickEvent\""),
                "Should not contain click event without unsafe permission. JSON: " + json);
    }

    @Test
    void testUnsafeTagsWithPermission() {
        when(player.hasPermission("chatformatter.click")).thenReturn(true);
        when(player.hasPermission("chatformatter.unsafe")).thenReturn(true);
        when(player.getDisplayName()).thenReturn("Player");
        when(player.getName()).thenReturn("Player");

        String rawMessage = "<click:run_command:'/op me'>Click me</click>";
        String jsonMessage = GsonComponentSerializer.gson().serialize(Component.text(rawMessage));

        ChatMessage chatMessage = new ChatMessage(player, Optional.empty(), jsonMessage);

        ChatRenderedMessage result = chatHandler.process(chatMessage);
        String json = result.jsonMessage();

        assertTrue(json.contains("\"clickEvent\""), "Should contain click event with unsafe permission. JSON: " + json);
    }

    @Test
    void testWildcardPermission() {
        when(player.hasPermission("chatformatter.*")).thenReturn(true);
        when(player.hasPermission("chatformatter.unsafe")).thenReturn(true);
        when(player.getDisplayName()).thenReturn("Player");
        when(player.getName()).thenReturn("Player");

        String rawMessage = "<click:run_command:'/op me'>Click me</click>";
        String jsonMessage = GsonComponentSerializer.gson().serialize(Component.text(rawMessage));

        ChatMessage chatMessage = new ChatMessage(player, Optional.empty(), jsonMessage);

        ChatRenderedMessage result = chatHandler.process(chatMessage);
        String json = result.jsonMessage();

        assertTrue(json.contains("\"clickEvent\""),
                "Should contain click event with wildcard permission. JSON: " + json);
    }
}
