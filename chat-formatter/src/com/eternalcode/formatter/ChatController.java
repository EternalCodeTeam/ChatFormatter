package com.eternalcode.formatter;

import com.eternalcode.formatter.legacy.Legacy;
import com.eternalcode.formatter.placeholder.PlaceholderRegistry;
import com.eternalcode.formatter.preparatory.ChatPreparatoryService;
import com.eternalcode.formatter.preparatory.ChatPrepareResult;
import com.eternalcode.formatter.template.TemplateService;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.platform.AudienceProvider;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Set;

class ChatController implements Listener {

    private static final GsonComponentSerializer GSON = GsonComponentSerializer.gson();

    private final AudienceProvider audienceProvider;
    private final MiniMessage miniMessage;

    private final ChatSettings settings;
    private final ChatRankProvider rankProvider;
    private final PlaceholderRegistry placeholderRegistry;
    private final TemplateService templateService;
    private final ChatPreparatoryService preparatoryService;

    ChatController(AudienceProvider audienceProvider, MiniMessage miniMessage, ChatSettings settings, ChatRankProvider rankProvider, PlaceholderRegistry placeholderRegistry, TemplateService templateService, ChatPreparatoryService preparatoryService) {
        this.audienceProvider = audienceProvider;
        this.miniMessage = miniMessage;
        this.settings = settings;
        this.rankProvider = rankProvider;
        this.placeholderRegistry = placeholderRegistry;
        this.templateService = templateService;
        this.preparatoryService = preparatoryService;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    void onPreChat(AsyncPlayerChatEvent event) {
        if (!this.settings.isPreFormatting()) {
            return;
        }

        String rank = this.rankProvider.getRank(event.getPlayer());
        String raw = this.settings.getRawFormat(rank);
        String format = Legacy.toBukkitFormat(raw);

        event.setFormat(format);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    void onChat(AsyncPlayerChatEvent event) {
        event.setCancelled(true);

        Player player = event.getPlayer();
        Identity identity = Identity.identity(player.getUniqueId());

        String message = this.settings.isPreFormatting()
            ? Legacy.toAdventureFormat(event.getFormat())
            : this.settings.getRawFormat(this.rankProvider.getRank(player));

        message = this.templateService.applyTemplates(message);
        message = this.placeholderRegistry.format(message, player);

        Component messageComponent = miniMessage.deserialize(message, this.messageTag(event), this.displaynameTag(event));

        Set<Player> recipients = event.getRecipients();

        if (!preparatoryService.isEmpty()) {
            ChatPrepareResult result = preparatoryService.prepare(player, recipients, GSON.serialize(messageComponent), event.getMessage());

            if (result.isCancelled()) {
                return;
            }

            messageComponent = GSON.deserialize(result.getRawMessage());
            recipients = result.getReceivers();
        }

        for (Player recipient : recipients) {
            Audience recipientAudience = this.audienceProvider.player(recipient.getUniqueId());

            recipientAudience.sendMessage(identity, messageComponent);
        }

        this.audienceProvider.console().sendMessage(identity, messageComponent);
    }

    private TagResolver.Single messageTag(AsyncPlayerChatEvent event) {
        String message = event.getMessage();
        Player player = event.getPlayer();

        return player.hasPermission("eternalmc.chat.color")
                ? Placeholder.parsed("message", message)
                : Placeholder.unparsed("message", Legacy.shadow(message));
    }


    private TagResolver.Single displaynameTag(AsyncPlayerChatEvent event) {
        return Placeholder.parsed("displayname", Legacy.clearSection(event.getPlayer().getDisplayName()));
    }

}
