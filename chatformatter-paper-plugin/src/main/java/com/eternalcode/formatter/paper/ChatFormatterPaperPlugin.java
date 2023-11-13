package com.eternalcode.formatter.paper;

import com.eternalcode.formatter.ChatFormatterPlugin;
import com.eternalcode.formatter.ChatHandler;
import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.Server;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class ChatFormatterPaperPlugin extends JavaPlugin {

    private ChatFormatterPlugin chatFormatter;

    @Override
    public void onEnable() {
        chatFormatter = new ChatFormatterPlugin(this);

        ChatHandler handler = chatFormatter.getChatHandler();
        Server server = this.getServer();
        PluginManager pluginManager = server.getPluginManager();

        pluginManager.registerEvent(AsyncChatEvent.class, handler, EventPriority.LOWEST, new PaperChatEventExecutor(), this, true);
    }

    @Override
    public void onDisable() {
        if (chatFormatter != null) {
            chatFormatter.close();
        }
    }

}
