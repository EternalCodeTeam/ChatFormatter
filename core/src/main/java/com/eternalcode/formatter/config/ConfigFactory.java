package com.eternalcode.formatter.config;

import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.serdes.OkaeriSerdesPack;
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer;

import java.nio.file.Path;

public class ConfigFactory {

    public static <T extends OkaeriConfig> T create(Class<T> configClass, Path path, OkaeriSerdesPack serdesPack) {
        return ConfigManager.create(configClass, (config -> {
            config.withBindFile(path);
            config.withConfigurer(new YamlBukkitConfigurer());
            config.withSerdesPack(serdesPack);
            config.withRemoveOrphans(true);
            config.saveDefaults();
            config.load(true);
        }));
    }
}
