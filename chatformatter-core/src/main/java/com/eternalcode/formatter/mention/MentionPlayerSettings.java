package com.eternalcode.formatter.mention;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

public class MentionPlayerSettings {

    private final File dataFile;
    private final Logger logger;
    private final Map<UUID, Boolean> settings = new HashMap<>();
    private final MentionConfig config;

    public MentionPlayerSettings(File dataFolder, Logger logger, MentionConfig config) {
        this.logger = logger;
        this.config = config;
        this.dataFile = new File(dataFolder, "mention-settings.yml");
        this.load();
    }

    public boolean isMentionSoundEnabled(UUID uuid) {
        return this.settings.getOrDefault(uuid, this.config.enabled);
    }

    public void setMentionSoundEnabled(UUID uuid, boolean enabled) {
        this.settings.put(uuid, enabled);
        this.save();
    }

    public boolean toggleMentionSound(UUID uuid) {
        boolean current = this.isMentionSoundEnabled(uuid);
        this.setMentionSoundEnabled(uuid, !current);
        return !current;
    }

    private void load() {
        if (!this.dataFile.exists()) {
            return;
        }

        try {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(this.dataFile);

            for (String key : config.getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(key);
                    boolean enabled = config.getBoolean(key, this.config.enabled);
                    this.settings.put(uuid, enabled);
                } catch (IllegalArgumentException e) {
                    this.logger.warning("Invalid UUID in mention-settings.yml: " + key);
                }
            }
        } catch (Exception e) {
            this.logger.warning("Failed to load mention settings: " + e.getMessage());
        }
    }

    private void save() {
        try {
            if (!this.dataFile.exists()) {
                this.dataFile.getParentFile().mkdirs();
                this.dataFile.createNewFile();
            }

            YamlConfiguration config = new YamlConfiguration();

            for (Map.Entry<UUID, Boolean> entry : this.settings.entrySet()) {
                config.set(entry.getKey().toString(), entry.getValue());
            }

            config.save(this.dataFile);
        } catch (IOException e) {
            this.logger.warning("Failed to save mention settings: " + e.getMessage());
        }
    }
}
