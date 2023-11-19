package com.eternalcode.formatter;

import org.bukkit.event.Listener;

public interface ChatHandler extends Listener {

    ChatRenderedMessage process(ChatMessage message);

}
