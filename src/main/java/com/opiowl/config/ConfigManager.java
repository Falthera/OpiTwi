package com.opiowl.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class ConfigManager {
    private final JavaPlugin plugin;
    private ConfigSnapshot snapshot;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public ConfigSnapshot reload() {
        try {
            plugin.reloadConfig();
            FileConfiguration config = plugin.getConfig();
            snapshot = ConfigSnapshot.from(config, plugin.getLogger());
            return snapshot;
        } catch (Exception ex) {
            plugin.getLogger().severe("Failed to reload config: " + ex.getMessage());
            return snapshot;
        }
    }

    public ConfigSnapshot getSnapshot() {
        return snapshot;
    }
}
