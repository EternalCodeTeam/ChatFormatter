package pl.eternalmc.chat.config;

import com.google.common.collect.ImmutableMap;
import net.dzikoysk.cdn.entity.Description;
import panda.utilities.StringUtils;
import pl.eternalmc.chat.ChatSettings;

import java.io.File;
import java.util.Map;

public class PluginConfig implements ChatSettings {


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

    @Override
    public boolean preFormatting() {
        return false;
    }

    @Override
    public String format(String rank) {
        return format.getOrDefault(rank, defaultFormat);
    }
}
