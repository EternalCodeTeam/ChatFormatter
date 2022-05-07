package pl.eternalmc.chat;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.platform.AudienceProvider;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import pl.eternalmc.chat.legacy.Legacy;
import pl.eternalmc.chat.placeholder.PlaceholderRegistry;

class ChatController implements Listener {

    private final AudienceProvider audienceProvider;
    private final MiniMessage miniMessage;
    private final ChatSettings settings;
    private final ChatRankProvider rankProvider;
    private final PlaceholderRegistry placeholderRegistry;

    ChatController(AudienceProvider audienceProvider, MiniMessage miniMessage, ChatSettings settings, ChatRankProvider rankProvider, PlaceholderRegistry placeholderRegistry) {
        this.audienceProvider = audienceProvider;
        this.miniMessage = miniMessage;
        this.settings = settings;
        this.rankProvider = rankProvider;
        this.placeholderRegistry = placeholderRegistry;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    void onPreChat(AsyncPlayerChatEvent event) {
        if (!this.settings.preFormatting()) {
            return;
        }

        String raw = this.settings.format(rankProvider.getRank(event.getPlayer()));
        String replaced = raw
            .replace("{displayname}", "%1$s")
            .replace("{message}", "%2$s");

        event.setFormat(replaced);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    void onChat(AsyncPlayerChatEvent event) {
        event.setCancelled(true);

        Player player = event.getPlayer();
        Identity identity = Identity.identity(player.getUniqueId());

        String raw = this.settings.preFormatting()
            ? event.getFormat()
            : this.settings.format(this.rankProvider.getRank(event.getPlayer()))
                .replace("{displayname}", "%1$s")
                .replace("{message}", "%2$s");

        String formatted = this.placeholderRegistry.format(raw, player);
        String message = String.format(formatted, player.getDisplayName(), event.getMessage());

        TextComponent colorsFromSection = Legacy.LEGACY_SERIALIZER.deserialize(message);
        String noColored = Legacy.LEGACY_AMPERSAND_SERIALIZER.serialize(colorsFromSection);

        Component component = miniMessage.deserialize(noColored);

        for (Player recipient : event.getRecipients()) {
            Audience recipientAudience = audienceProvider.player(recipient.getUniqueId());

            recipientAudience.sendMessage(identity, component);
        }
    }

}
