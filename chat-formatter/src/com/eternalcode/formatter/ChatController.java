package com.eternalcode.formatter;

import com.eternalcode.formatter.legacy.Legacy;
import com.eternalcode.formatter.legacy.LegacyPostMessageProcessor;
import com.eternalcode.formatter.legacy.LegacyPreProcessor;
import com.eternalcode.formatter.placeholder.PlaceholderRegistry;
import com.eternalcode.formatter.preparatory.ChatPreparatoryService;
import com.eternalcode.formatter.preparatory.ChatPrepareResult;
import com.eternalcode.formatter.adventure.PlayerSignedMessage;
import com.eternalcode.formatter.template.TemplateService;
import com.google.common.collect.ImmutableMap;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.chat.ChatType;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.platform.AudienceProvider;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

class ChatController implements Listener {

    private static final String PERMISSION_ALL = "chat.*";
    private static final String PERMISSION_LEGACY = "chat.legacycolor";
    private static final Map<String, TagResolver> TAG_RESOLVERS_BY_PERMISSION = new ImmutableMap.Builder<String, TagResolver>()
        .put("chat.color", StandardTags.color())
        .put("chat.decorations.*", StandardTags.decorations())
        .put("chat.decorations.bold", StandardTags.decorations(TextDecoration.BOLD))
        .put("chat.decorations.italic", StandardTags.decorations(TextDecoration.ITALIC))
        .put("chat.decorations.underlined", StandardTags.decorations(TextDecoration.UNDERLINED))
        .put("chat.decorations.strikethrough", StandardTags.decorations(TextDecoration.STRIKETHROUGH))
        .put("chat.decorations.obfuscated", StandardTags.decorations(TextDecoration.OBFUSCATED))
        .put("chat.reset", StandardTags.reset())
        .put("chat.gradient", StandardTags.gradient())
        .put("chat.hover", StandardTags.hoverEvent())
        .put("chat.click", StandardTags.clickEvent())
        .put("chat.insertion", StandardTags.insertion())
        .put("chat.font", StandardTags.font())
        .put("chat.transition", StandardTags.transition())
        .put("chat.translatable", StandardTags.translatable())
        .put("chat.selector", StandardTags.selector())
        .put("chat.keybind", StandardTags.keybind())
        .put("chat.newline", StandardTags.newline())
        .build();

    private static final GsonComponentSerializer GSON = GsonComponentSerializer.gson();
    private static final MiniMessage MESSAGE_DESERIALIZER = MiniMessage.builder()
        .tags(TagResolver.empty())
        .preProcessor(new LegacyPreProcessor())
        .postProcessor(new LegacyPostMessageProcessor())
        .build();

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

        Component messageComponent = miniMessage.deserialize(message, this.createTagResolvers(event));

        Set<Player> recipients = event.getRecipients();

        if (!preparatoryService.isEmpty()) {
            ChatPrepareResult result = preparatoryService.prepare(player, recipients, GSON.serialize(messageComponent), event.getMessage());

            if (result.isCancelled()) {
                return;
            }

            messageComponent = GSON.deserialize(result.getRawMessage());
            recipients = result.getReceivers();
        }

        PlayerSignedMessage signedMessage = new PlayerSignedMessage(messageComponent, identity);
        ChatType.Bound chatType = ChatType.CHAT.bind(Component.text("chat"));

        for (Player recipient : recipients) {
            Audience recipientAudience = this.audienceProvider.player(recipient.getUniqueId());

            recipientAudience.sendMessage(signedMessage, chatType);
        }

        this.audienceProvider.console().sendMessage(signedMessage, chatType);
    }

    private TagResolver createTagResolvers(AsyncPlayerChatEvent event) {
        String message = event.getMessage();
        Player player = event.getPlayer();

        TagResolver.Single displayNamePlaceholder = Placeholder.parsed("displayname", Legacy.clearSection(event.getPlayer().getDisplayName()));

        String safeMessage = player.hasPermission(PERMISSION_LEGACY)
            ? message
            : Legacy.ampersandToPlaceholder(message);
        Component componentMessage = MESSAGE_DESERIALIZER.deserialize(safeMessage, this.messageResolver(player));
        TagResolver.Single messagePlaceholder = Placeholder.component("message", componentMessage);

        return TagResolver.resolver(displayNamePlaceholder, messagePlaceholder);
    }

    private TagResolver messageResolver(Player player) {
        List<TagResolver> tagResolvers = new ArrayList<>();

        if (player.hasPermission(PERMISSION_ALL)) {
            tagResolvers.addAll(TAG_RESOLVERS_BY_PERMISSION.values());
            return TagResolver.resolver(tagResolvers);
        }

        for (Map.Entry<String, TagResolver> entry : TAG_RESOLVERS_BY_PERMISSION.entrySet()) {
            if (player.hasPermission(entry.getKey())) {
                tagResolvers.add(entry.getValue());
            }
        }

        return TagResolver.resolver(tagResolvers);
    }

}
