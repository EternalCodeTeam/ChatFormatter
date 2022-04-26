package pl.eternalmc.chat.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import pl.eternalmc.chat.utils.ChatUtils;

public class PlayerChatListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        String message = event.getMessage();
        Player player = event.getPlayer();

        String formatterMessage = "%luckperms_displayname% %nick% -> %message%"
            .replace("%nick%", player.getName())
            .replace("%message%", message);

        event.setFormat(ChatUtils.sanitize(player, formatterMessage));
    }
}
