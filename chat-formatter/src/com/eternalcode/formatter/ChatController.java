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

    private final static GsonComponentSerializer GSON = GsonComponentSerializer.gson();

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
            : this.settings.format(this.rankProvider.getRank(player));

        String withTemplates = this.templateService.applyTemplates(raw);
        String withFormat = this.placeholderRegistry.format(withTemplates, player);
        String withMessage = String.format(withFormat, player.getDisplayName(), "<message>");
        String decolor = Legacy.deColor(withMessage);

        TagResolver.Single shadowed = player.hasPermission("eternalmc.chat.color")
            ? Placeholder.parsed("message", event.getMessage())
            : Placeholder.unparsed("message", Legacy.shadow(event.getMessage()));

        Component message = miniMessage.deserialize(decolor, shadowed);

        Set<Player> recipients = event.getRecipients();

        if (!preparatoryService.isEmpty()) {
            ChatPrepareResult result = preparatoryService.prepare(player, recipients, GSON.serialize(message), event.getMessage());

            if (result.isCancelled()) {
                return;
            }

            message = GSON.deserialize(result.getRawMessage());
            recipients = result.getReceivers();
        }

        for (Player recipient : recipients) {
            Audience recipientAudience = audienceProvider.player(recipient.getUniqueId());

            recipientAudience.sendMessage(identity, message);
        }

        audienceProvider.console().sendMessage(identity, message);
    }

}
