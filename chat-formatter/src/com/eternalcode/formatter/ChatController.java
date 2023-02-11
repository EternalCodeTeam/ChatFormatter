package com.eternalcode.formatter;

import com.eternalcode.formatter.adventure.TextColorTagResolver;
import com.eternalcode.formatter.legacy.Legacy;
import com.eternalcode.formatter.legacy.LegacyPostMessageProcessor;
import com.eternalcode.formatter.legacy.LegacyPreProcessor;
import com.eternalcode.formatter.template.TemplateService;
import com.eternalcode.formatter.placeholder.PlaceholderRegistry;
import com.eternalcode.formatter.preparatory.ChatPreparatoryService;
import com.eternalcode.formatter.preparatory.ChatPrepareResult;
import com.eternalcode.formatter.adventure.PlayerSignedMessage;
import com.google.common.collect.ImmutableMap;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.chat.ChatType;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.platform.AudienceProvider;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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

    private static final String PERMISSION_ALL = "chatformatter.*";
    private static final String PERMISSION_LEGACY = "chatformatter.legacycolor";
    private static final Map<String, TagResolver> TAG_RESOLVERS_BY_PERMISSION = new ImmutableMap.Builder<String, TagResolver>()
        .put("chatformatter.color.*", StandardTags.color())
        .put("chatformatter.color.black", TextColorTagResolver.of(NamedTextColor.BLACK))
        .put("chatformatter.color.dark_blue", TextColorTagResolver.of(NamedTextColor.DARK_BLUE))
        .put("chatformatter.color.dark_green", TextColorTagResolver.of(NamedTextColor.DARK_GREEN))
        .put("chatformatter.color.dark_aqua", TextColorTagResolver.of(NamedTextColor.DARK_AQUA))
        .put("chatformatter.color.dark_red", TextColorTagResolver.of(NamedTextColor.DARK_RED))
        .put("chatformatter.color.dark_purple", TextColorTagResolver.of(NamedTextColor.DARK_PURPLE))
        .put("chatformatter.color.gold", TextColorTagResolver.of(NamedTextColor.GOLD))
        .put("chatformatter.color.gray", TextColorTagResolver.of(NamedTextColor.GRAY))
        .put("chatformatter.color.dark_gray", TextColorTagResolver.of(NamedTextColor.DARK_GRAY))
        .put("chatformatter.color.blue", TextColorTagResolver.of(NamedTextColor.BLUE))
        .put("chatformatter.color.green", TextColorTagResolver.of(NamedTextColor.GREEN))
        .put("chatformatter.color.aqua", TextColorTagResolver.of(NamedTextColor.AQUA))
        .put("chatformatter.color.red", TextColorTagResolver.of(NamedTextColor.RED))
        .put("chatformatter.color.light_purple", TextColorTagResolver.of(NamedTextColor.LIGHT_PURPLE))
        .put("chatformatter.color.yellow", TextColorTagResolver.of(NamedTextColor.YELLOW))
        .put("chatformatter.color.white", TextColorTagResolver.of(NamedTextColor.WHITE))
        .put("chatformatter.decorations.*", StandardTags.decorations())
        .put("chatformatter.decorations.bold", StandardTags.decorations(TextDecoration.BOLD))
        .put("chatformatter.decorations.italic", StandardTags.decorations(TextDecoration.ITALIC))
        .put("chatformatter.decorations.underlined", StandardTags.decorations(TextDecoration.UNDERLINED))
        .put("chatformatter.decorations.strikethrough", StandardTags.decorations(TextDecoration.STRIKETHROUGH))
        .put("chatformatter.decorations.obfuscated", StandardTags.decorations(TextDecoration.OBFUSCATED))
        .put("chatformatter.reset", StandardTags.reset())
        .put("chatformatter.gradient", StandardTags.gradient())
        .put("chatformatter.hover", StandardTags.hoverEvent())
        .put("chatformatter.click", StandardTags.clickEvent())
        .put("chatformatter.insfertion", StandardTags.insertion())
        .put("chatformatter.font", StandardTags.font())
        .put("chatformatter.transition", StandardTags.transition())
        .put("chatformatter.translatable", StandardTags.translatable())
        .put("chatformatter.selector", StandardTags.selector())
        .put("chatformatter.keybind", StandardTags.keybind())
        .put("chatformatter.newline", StandardTags.newline())
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
            return TagResolver.standard();
        }

        for (Map.Entry<String, TagResolver> entry : TAG_RESOLVERS_BY_PERMISSION.entrySet()) {
            if (player.hasPermission(entry.getKey())) {
                tagResolvers.add(entry.getValue());
            }
        }

        return TagResolver.resolver(tagResolvers);
    }

}
