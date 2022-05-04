package pl.eternalmc.chat.hook;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import pl.eternalmc.chat.placeholder.PlayerPlaceholderStack;

public class PlaceholderAPIStack implements PlayerPlaceholderStack {

    @Override
    public String apply(String text, Player target) {
        return PlaceholderAPI.setPlaceholders(target, text);
    }

}
