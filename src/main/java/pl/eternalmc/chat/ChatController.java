package pl.eternalmc.chat;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.platform.AudienceProvider;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import pl.eternalmc.chat.placeholder.PlaceholderRegistry;

class ChatController implements Listener {

    private final AudienceProvider audienceProvider;
    private final MiniMessage miniMessage;
    private final ChatSettings chatSettings;
    private final ChatRankProvider chatRankProvider;
    private final PlaceholderRegistry placeholderRegistry;

    ChatController(AudienceProvider audienceProvider, MiniMessage miniMessage, ChatSettings chatSettings, ChatRankProvider chatRankProvider, PlaceholderRegistry placeholderRegistry) {
        this.audienceProvider = audienceProvider;
        this.miniMessage = miniMessage;
        this.chatSettings = chatSettings;
        this.chatRankProvider = chatRankProvider;
        this.placeholderRegistry = placeholderRegistry;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    void onPreChat(AsyncPlayerChatEvent event) {
        String raw = this.chatSettings.format(chatRankProvider.getRank(event.getPlayer()));
        String replaced = raw
            .replace("%message%", "%1$s")
            .replace("%player%", "%2$s");

        event.setFormat(replaced);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    void onChat(AsyncPlayerChatEvent event) {
        event.setCancelled(true);

        Player player = event.getPlayer();
        Identity identity = Identity.identity(player.getUniqueId());

        String raw = event.getFormat();
        String formatted = this.placeholderRegistry.format(raw, player);
        String message = String.format(raw, player.getDisplayName(), event.getMessage());

        Component deserialized = miniMessage.deserialize(message);

        for (Player recipient : event.getRecipients()) {
            Audience recipientAudience = audienceProvider.player(recipient.getUniqueId());

            recipientAudience.sendMessage(identity, deserialized);
        }
    }

}
