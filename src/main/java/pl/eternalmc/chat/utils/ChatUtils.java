package pl.eternalmc.chat.utils;

import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import panda.std.stream.PandaStream;
import pl.eternalmc.chat.ChatPlugin;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ChatUtils {

    private static final Pattern HEX_PATTERN = Pattern.compile("#[0-9a-fA-F]{6}");

    private ChatUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static String color(String text) {
        if (Bukkit.getVersion().contains("1.16")) {
            Matcher match = HEX_PATTERN.matcher(text);
            while (match.find()) {
                String color = text.substring(match.start(), match.end());
                text = text.replace(color, ChatColor.of(color) + "");
                match = HEX_PATTERN.matcher(text);
            }
        }
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static List<String> color(Iterable<String> texts) {
        return PandaStream.of(texts).map(ChatUtils::color).toList();
    }

    public static String sanitize(Player player, String text) {
            return color(PlaceholderAPI.setPlaceholders(player, text));
    }

    public static List<String> sanitize(Player player, List<String> texts) {
            return color(PlaceholderAPI.setPlaceholders(player, texts));
    }
}
