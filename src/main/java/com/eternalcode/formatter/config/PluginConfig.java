package com.eternalcode.formatter.config;

import com.google.common.collect.ImmutableMap;
import net.dzikoysk.cdn.entity.Description;
import panda.utilities.StringUtils;
import com.eternalcode.formatter.ChatSettings;
import com.eternalcode.formatter.placeholder.PlaceholderStack;

import java.util.Map;

public class PluginConfig implements ChatSettings, PlaceholderStack {

    @Description(StringUtils.EMPTY)
    @Description("# ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ #")
    @Description("# ~~~~~~~ EternalMC :: ChatFormatter ~~~~~~~ #")
    @Description("# ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ #")
    @Description(StringUtils.EMPTY)

    @Description("# Czy włączyć pre formatowanie chatu? (aby inne pluginy mogły dołączyć własne prefixy itd.)")
    @Description("# INFO: Ta opcja wymaga użycia znaczników {displayname} i {message} w każdej wiadomości")
    public boolean preFormatting = false;

    public String defaultFormat = "{displayname} -> {message}";

    @Description({ StringUtils.EMPTY, "# Format czatu dla rang (Vault)" })
    public Map<String, String> format = new ImmutableMap.Builder<String, String>()
        .put("default", "{displayname} -> {message}")
        .put("admin", "&c{displayname} &c-> {message}")
        .build();

    @Description({ StringUtils.EMPTY, "# Placeholders " })
    public Map<String, String> placeholders = new ImmutableMap.Builder<String, String>()
        .put("{custom}", "SIEMA")
        .build();

    @Override
    public boolean preFormatting() {
        return false;
    }

    @Override
    public String format(String rank) {
        return format.getOrDefault(rank, defaultFormat);
    }

    @Override
    public String apply(String text) {
        String value = text;

        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            value = value.replace(entry.getKey(), entry.getValue());
        }

        return value;
    }

}
