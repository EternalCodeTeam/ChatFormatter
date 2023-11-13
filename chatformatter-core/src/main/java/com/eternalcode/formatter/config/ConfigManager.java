package com.eternalcode.formatter.config;

import com.eternalcode.formatter.template.Template;
import net.dzikoysk.cdn.Cdn;
import net.dzikoysk.cdn.CdnFactory;
import net.dzikoysk.cdn.source.Resource;
import net.dzikoysk.cdn.source.Source;

import java.io.File;

public class ConfigManager {

    private final Cdn cdn = CdnFactory
        .createYamlLike()
        .getSettings()
        .withComposer(Template.class, new TemplateComposer())
        .build();

    private final PluginConfig pluginConfig;
    private final File dataFolder;

    public ConfigManager(File dataFolder) {
        this.pluginConfig = new PluginConfig();
        this.dataFolder = dataFolder;
    }

    public void loadAndRenderConfigs() {
        this.loadAndRender(this.pluginConfig, "config.yml");
    }

    public <T> void loadAndRender(T config, String file) {
        Resource resource = Source.of(this.dataFolder, file);

        this.cdn.load(resource, config)
            .orThrow(RuntimeException::new);

        this.cdn.render(config, resource)
            .orThrow(RuntimeException::new);
    }

    public PluginConfig getPluginConfig() {
        return this.pluginConfig;
    }

}
