package com.eternalcode.formatter.paper;

import com.eternalcode.formatter.preparatory.ChatPreparatory;
import com.eternalcode.formatter.preparatory.ChatPrepareResult;
import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.ForwardingAudience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

import java.util.HashSet;
import java.util.Set;

class ChatPaperPreparatory implements ChatPreparatory {

    private final static GsonComponentSerializer GSON = GsonComponentSerializer.gson();
    private final PluginManager pluginManager;

    ChatPaperPreparatory(PluginManager pluginManager) {
        this.pluginManager = pluginManager;
    }

    @Override
    public ChatPrepareResult prepare(Player player, Set<Player> receivers, String rawJson, String message, boolean canceled) {
        ChatRenderer renderer = (source, sourceDisplayName, ignoredMessage, viewer) -> GSON.deserialize(rawJson);
        Component messageComponent = GSON.deserialize(message);
        HashSet<Audience> audiences = new HashSet<>(receivers);
        AsyncChatEvent event = new AsyncChatEvent(true, player, audiences, renderer, messageComponent, messageComponent);

        event.setCancelled(canceled);
        this.pluginManager.callEvent(event);

        ForwardingAudience audience = Audience.audience(audiences);
        String serialized = GSON.serialize(event.renderer().render(player, player.displayName(), event.message(), audience));

        return new ChatPrepareResult(serialized, receivers, event.isCancelled());
    }

}
