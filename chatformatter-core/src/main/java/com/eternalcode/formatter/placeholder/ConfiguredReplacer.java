package com.eternalcode.formatter.placeholder;

import com.eternalcode.formatter.config.PluginConfig;
import java.util.Map;
import org.bukkit.entity.Player;

public class ConfiguredReplacer implements Replacer {

    private final PluginConfig pluginConfig;

    public ConfiguredReplacer(PluginConfig pluginConfig) {
        this.pluginConfig = pluginConfig;
    }

    @Override
    public String apply(String text, Player target) {
        String value = text;

        for (Map.Entry<String, String> entry : this.pluginConfig.placeholders.entrySet()) {
            value = value.replace(entry.getKey(), entry.getValue());
        }

        return value;
    }

}
