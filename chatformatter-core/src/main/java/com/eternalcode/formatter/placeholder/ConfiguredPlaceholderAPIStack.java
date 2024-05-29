package com.eternalcode.formatter.placeholder;

import com.eternalcode.formatter.config.PluginConfig;
import java.util.Map;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

public class ConfiguredPlaceholderAPIStack implements PlayerPlaceholderStack {

    private final PluginConfig pluginConfig;

    public ConfiguredPlaceholderAPIStack(PluginConfig pluginConfig) {
        this.pluginConfig = pluginConfig;
    }

    @Override
    public String apply(String text, Player target) {
        String value = text;

        for (Map.Entry<String, String> entry : this.pluginConfig.placeholders.entrySet()) {
            value = value.replace(entry.getKey(), entry.getValue());
        }

        value = PlaceholderAPI.setPlaceholders(target, value);

        return value;
    }

}
