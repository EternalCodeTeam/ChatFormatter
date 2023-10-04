package com.eternalcode.formatter.paper;

import com.eternalcode.formatter.ChatFormatter;
import com.eternalcode.formatter.ChatFormatterProvider;
import com.eternalcode.formatter.preparatory.ChatPreparatoryService;
import org.bukkit.plugin.java.JavaPlugin;

public class ChatFormatterPaperSupportPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        ChatFormatter chatFormatter = ChatFormatterProvider.get();
        ChatPreparatoryService preparatoryService = chatFormatter.getChatPreparatoryService();

        preparatoryService.registerPreparatory(new ChatPaperPreparatory(this.getServer().getPluginManager()));
    }

}
