package com.eternalcode.formatter;

import com.eternalcode.formatter.adventure.AdventureUrlPostProcessor;
import com.eternalcode.formatter.adventure.TextColorTagResolver;
import com.eternalcode.formatter.legacy.Legacy;
import com.eternalcode.formatter.placeholder.PlaceholderRegistry;
import com.eternalcode.formatter.rank.ChatRankProvider;
import com.eternalcode.formatter.template.TemplateService;
import com.google.common.collect.ImmutableMap;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.json.JSONOptions;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

class ChatHandlerImpl implements ChatHandler {

    private static final String PERMISSION_ALL = "chatformatter.*";
    private static final Map<String, TagResolver> TAG_RESOLVERS_BY_PERMISSION = new ImmutableMap.Builder<String, TagResolver>()
        .put("chatformatter.decorations.*", StandardTags.decorations())
        .put("chatformatter.decorations.bold", StandardTags.decorations(TextDecoration.BOLD))
        .put("chatformatter.decorations.italic", StandardTags.decorations(TextDecoration.ITALIC))
        .put("chatformatter.decorations.underlined", StandardTags.decorations(TextDecoration.UNDERLINED))
        .put("chatformatter.decorations.strikethrough", StandardTags.decorations(TextDecoration.STRIKETHROUGH))
        .put("chatformatter.decorations.obfuscated", StandardTags.decorations(TextDecoration.OBFUSCATED))
        .put("chatformatter.reset", StandardTags.reset())
        .put("chatformatter.newline", StandardTags.newline())
        .put("chatformatter.shadow", StandardTags.shadowColor())
        .put("chatformatter.gradient", StandardTags.gradient())
        .put("chatformatter.rainbow", StandardTags.rainbow())
        .put("chatformatter.pride", StandardTags.pride())
        .put("chatformatter.transition", StandardTags.transition())
        .put("chatformatter.hover", StandardTags.hoverEvent())
        .put("chatformatter.click", StandardTags.clickEvent())
        .put("chatformatter.insertion", StandardTags.insertion())
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
        .put("chatformatter.score", StandardTags.score())
        .put("chatformatter.selector", StandardTags.selector())
        .put("chatformatter.translatable", TagResolver.resolver(StandardTags.translatable(), StandardTags.translatableFallback()))
        .put("chatformatter.font", StandardTags.font())
        .put("chatformatter.keybind", StandardTags.keybind())
        .put("chatformatter.nbt", StandardTags.nbt())
        .build();

    private static final GsonComponentSerializer GSON = GsonComponentSerializer.builder()
        .editOptions(builder -> builder.values(JSONOptions.compatibility()))
        .build();

    private static final MiniMessage EMPTY_MESSAGE_DESERIALIZER = MiniMessage.builder()
        .postProcessor(new AdventureUrlPostProcessor())
        .tags(TagResolver.empty())
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
        Optional<Player> viewer = chatMessage.viewer();

        String format = this.settings.getRawFormat(this.rankProvider.getRank(sender));

        format = this.templateService.applyTemplates(format);
        format = viewer.isEmpty()
            ? this.placeholderRegistry.format(format, sender)
            : this.placeholderRegistry.format(format, sender, viewer.get());

        format = Legacy.legacyToAdventure(format);

        Component renderedMessage = this.miniMessage.deserialize(format, this.createTags(chatMessage));

        return new ChatRenderedMessage(sender, GSON.serialize(renderedMessage));
    }

    private TagResolver createTags(ChatMessage chatMessage) {
        Player sender = chatMessage.sender();

        Component message = GSON.deserialize(chatMessage.jsonMessage());
        String serialize = LegacyComponentSerializer.legacySection().serialize(message);

        TagResolver.Single displayNamePlaceholder = displayNamePlaceholder(sender);
        TagResolver.Single namePlaceholder = namePlaceholder(sender);
        TagResolver.Single messagePlaceholder = messagePlaceholder(sender, serialize);

        return TagResolver.resolver(displayNamePlaceholder, namePlaceholder, messagePlaceholder);
    }

    private TagResolver.Single displayNamePlaceholder(Player sender) {
        return Placeholder.parsed("displayname", Legacy.legacyToAdventure(sender.getDisplayName()));
    }

    private TagResolver.Single namePlaceholder(Player sender) {
        return Placeholder.parsed("name", sender.getName());
    }

    private TagResolver.Single messagePlaceholder(Player sender, String rawMessage) {
        TagResolver permittedTags = this.providePermittedTags(sender);
        rawMessage = Legacy.legacyToAdventure(rawMessage, permission -> sender.hasPermission(permission));
        Component componentMessage = EMPTY_MESSAGE_DESERIALIZER.deserialize(rawMessage, permittedTags);
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
