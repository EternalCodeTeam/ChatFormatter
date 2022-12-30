package com.eternalcode.formatter.paper;

import com.eternalcode.formatter.paper.adventure.PaperSignedMessage;
import com.eternalcode.formatter.paper.injector.DependencyInjector;
import com.eternalcode.formatter.preparatory.ChatPreparatory;
import com.eternalcode.formatter.preparatory.ChatPrepareResult;
import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.ForwardingAudience;
import net.kyori.adventure.chat.SignedMessage;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

import java.util.HashSet;
import java.util.Set;

class ChatPaperPreparatory implements ChatPreparatory {

    private static final GsonComponentSerializer GSON = GsonComponentSerializer.gson();

    private final PluginManager pluginManager;

    ChatPaperPreparatory(PluginManager pluginManager) {
        this.pluginManager = pluginManager;
    }

    @Override
    public ChatPrepareResult prepare(Player player, Set<Player> receivers, String jsonFormat, String message, boolean canceled) {
        ChatRenderer renderer = (source, sourceDisplayName, ignoredMessage, viewer) -> GSON.deserialize(jsonFormat);
        Component messageComponent = Component.text(message);
        HashSet<Audience> audiences = new HashSet<>(receivers);

        DependencyInjector injector = new DependencyInjector()
            .register(boolean.class, true)
            .register(Player.class, player)
            .register(Set.class, audiences)
            .register(ChatRenderer.class, renderer)
            .register(Component.class, messageComponent)
            .register(SignedMessage.class, new PaperSignedMessage(messageComponent, Identity.identity(player.getUniqueId())));

        AsyncChatEvent event = injector.newInstance(AsyncChatEvent.class);

        event.setCancelled(canceled);
        this.pluginManager.callEvent(event);

        ForwardingAudience audience = Audience.audience(audiences);
        String serialized = GSON.serialize(event.renderer().render(player, player.displayName(), event.message(), audience));

        return new ChatPrepareResult(serialized, receivers, event.isCancelled());
    }

}
