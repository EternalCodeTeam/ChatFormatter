package pl.eternalmc.chat.config;

import net.dzikoysk.cdn.Cdn;
import net.dzikoysk.cdn.CdnFactory;
import pl.eternalmc.chat.config.impl.PluginConfig;

import java.io.File;

public class ConfigManager {

    private final Cdn cdn = CdnFactory
        .createYamlLike()
        .getSettings()
        .build();

    private final PluginConfig pluginConfig;

    public ConfigManager(File dataFolder) {
        this.pluginConfig = new PluginConfig(dataFolder, "config.yml");
    }

    public void loadAndRenderConfigs() {
        this.loadAndRender(pluginConfig);
    }

    public <T extends ConfigWithResource> void loadAndRender(T config) {
        cdn.load(config.getResource(), config)
            .orElseThrow(RuntimeException::new);

        cdn.render(config, config.getResource())
            .orElseThrow(RuntimeException::new);
    }

    public <T extends ConfigWithResource> void render(T config) {
        cdn.render(config, config.getResource())
            .orElseThrow(RuntimeException::new);
    }

    public PluginConfig getPluginConfig() {
        return pluginConfig;
    }
}
