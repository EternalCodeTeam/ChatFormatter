package pl.eternalmc.chat.config.impl;

import net.dzikoysk.cdn.entity.Description;
import net.dzikoysk.cdn.source.Resource;
import panda.utilities.StringUtils;
import pl.eternalmc.chat.config.AbstractConfigWithResource;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class PluginConfig extends AbstractConfigWithResource {


    @Description(StringUtils.EMPTY)
    @Description("# ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ #")
    @Description("# ~~~~~~~ EternalMC :: ChatFormatter ~~~~~~~ #")
    @Description("# ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ #")
    @Description(StringUtils.EMPTY)

    @Description({ StringUtils.EMPTY, "# Format czatu, lepsze rozwiązanie niż LPC, pobieramy od razu prefix z placeholdera" })
    public String format = "%luckperms_displayname% %nick% -> %message%";

    @Description(StringUtils.EMPTY)
    public String hoverMessage = "Guwno\nn1\nn2";

    @Description(StringUtils.EMPTY)
    public String runCommand = "/msg %nick%";

    public PluginConfig(File folder, String child) {
        super(folder, child);
    }
}
