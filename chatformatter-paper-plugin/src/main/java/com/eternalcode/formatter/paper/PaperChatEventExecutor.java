package com.eternalcode.formatter.paper;

import com.eternalcode.formatter.ChatHandler;
import com.eternalcode.formatter.ChatMessage;
import com.eternalcode.formatter.ChatRenderedMessage;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

class PaperChatEventExecutor implements EventExecutor {

    private final static GsonComponentSerializer GSON = GsonComponentSerializer.gson();

    @Override
    public void execute(@NotNull Listener listener, @NotNull Event event) throws EventException {
        if (!(listener instanceof ChatHandler handler)) {
            throw new EventException("Listener is not a ChatHandler");
        }

        if (!(event instanceof AsyncChatEvent paperEvent)) {
            return;
        }

        paperEvent.renderer((source, sourceDisplayName, message, viewer) -> {
            String jsonMessage = GSON.serialize(message);

            Player player = null;
            Optional<UUID> viewerUUID = viewer.get(Identity.UUID);
            if (viewerUUID.isPresent()) {
                player = source.getServer().getPlayer(viewerUUID.get());
            }

            ChatMessage chatMessage = new ChatMessage(source, player, jsonMessage);
            ChatRenderedMessage result = handler.process(chatMessage);

            return GSON.deserialize(result.jsonMessage());
        });
    }

}
