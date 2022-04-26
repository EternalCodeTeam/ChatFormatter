package pl.eternalmc.chat.listener;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import pl.eternalmc.chat.config.impl.PluginConfig;
import pl.eternalmc.chat.utils.ChatUtils;

public class PlayerChatListener implements Listener {

    public final PluginConfig pluginConfig;

    public PlayerChatListener(PluginConfig pluginConfig) {
        this.pluginConfig = pluginConfig;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        String message = event.getMessage();
        Player player = event.getPlayer();

        String formatterMessage = pluginConfig.format
            .replace("%nick%", player.getName())
            .replace("%message%", message);

        TextComponent textComponent = new TextComponent(ChatUtils.sanitize(player, formatterMessage));
        textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(pluginConfig.hoverMessage).create()));
        textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, pluginConfig.runCommand.replace("%nick%", player.getName()) + " "));

        Bukkit.getServer().spigot().broadcast(textComponent);
        event.setCancelled(true);
    }
}
