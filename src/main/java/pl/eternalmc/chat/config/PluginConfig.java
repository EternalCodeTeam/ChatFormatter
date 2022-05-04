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

    String defaultFormat = "%luckperms_displayname% %nick% -> %message%";

    @Description({ StringUtils.EMPTY, "# Format czatu, lepsze rozwiązanie niż LPC, pobieramy od razu prefix z placeholdera" })
    Map<String, String> format = new ImmutableMap.Builder<String, String>()
        .put("default", "%luckperms_displayname% %nick% -> %message%")
        .put("admin", "%luckperms_displayname% %nick% -> %message%")
        .build();

    @Override
    public String format(String rank) {
        return format.getOrDefault(rank, defaultFormat);
    }
}
