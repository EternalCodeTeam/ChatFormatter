package com.eternalcode.formatter;

import static net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacySection;

import com.eternalcode.formatter.adventure.TextColorTagResolver;
import com.eternalcode.formatter.legacy.Legacy;
import com.eternalcode.formatter.legacy.LegacyPostMessageProcessor;
import com.eternalcode.formatter.legacy.LegacyPreProcessor;
import com.eternalcode.formatter.rank.ChatRankProvider;
import com.eternalcode.formatter.template.TemplateService;
import com.eternalcode.formatter.placeholder.PlaceholderRegistry;
import com.google.common.collect.ImmutableMap;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class ChatHandlerImpl implements ChatHandler {

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
    private static final MiniMessage EMPTY_MESSAGE_DESERIALIZER = MiniMessage.builder()
        .tags(TagResolver.empty())
        .preProcessor(new LegacyPreProcessor())
        .postProcessor(new LegacyPostMessageProcessor())
        .build();

    private final MiniMessage miniMessage;

    private final ChatSettings settings;
    private final ChatRankProvider rankProvider;
    private final PlaceholderRegistry placeholderRegistry;
    private final TemplateService templateService;

    ChatHandlerImpl(MiniMessage miniMessage, ChatSettings settings, ChatRankProvider rankProvider, PlaceholderRegistry placeholderRegistry, TemplateService templateService) {
        this.miniMessage = miniMessage;
        this.settings = settings;
        this.rankProvider = rankProvider;
        this.placeholderRegistry = placeholderRegistry;
        this.templateService = templateService;
    }

    @Override
    public ChatRenderedMessage process(ChatMessage chatMessage) {
        Player sender = chatMessage.sender();
        Player player = chatMessage.player();

        String format = this.settings.getRawFormat(this.rankProvider.getRank(sender));

        format = this.templateService.applyTemplates(format);
        try {
            format = this.placeholderRegistry.format(format, sender, player);
        } catch (Exception ignored) {
            format = this.placeholderRegistry.format(format, sender);
        }

        format = Legacy.clearSection(format);
        format = Legacy.legacyToAdventure(format);

        Component renderedMessage = this.miniMessage.deserialize(format, this.createTags(chatMessage));

        return new ChatRenderedMessage(sender, GSON.serialize(renderedMessage));
    }

    private TagResolver createTags(ChatMessage chatMessage) {
        Player sender = chatMessage.sender();

        Component message = GSON.deserialize(chatMessage.jsonMessage());
        String serialize = legacySection().serialize(message);

        TagResolver.Single displayNamePlaceholder = displayNamePlaceholder(sender);
        TagResolver.Single namePlaceholder = namePlaceholder(sender);
        TagResolver.Single messagePlaceholder = messagePlaceholder(sender, serialize);

        return TagResolver.resolver(displayNamePlaceholder, namePlaceholder, messagePlaceholder);
    }

    private TagResolver.Single displayNamePlaceholder(Player sender) {
        return Placeholder.parsed("displayname", Legacy.clearSection(sender.getDisplayName()));
    }

    private TagResolver.Single namePlaceholder(Player sender) {
        return Placeholder.parsed("name", Legacy.clearSection(sender.getName()));
    }

    private TagResolver.Single messagePlaceholder(Player sender, String rawMessage) {
        String safeMessage = sender.hasPermission(PERMISSION_LEGACY)
                ? rawMessage
                : Legacy.ampersandToPlaceholder(rawMessage);

        Component componentMessage = EMPTY_MESSAGE_DESERIALIZER.deserialize(safeMessage, this.providePermittedTags(sender));

        return Placeholder.component("message", componentMessage);
    }

    private TagResolver providePermittedTags(Player player) {
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
