package com.opiowl.memory;

import com.opiowl.config.ConfigSnapshot;
import com.opiowl.core.Mode;
import com.opiowl.core.Subsystem;
import org.bukkit.plugin.java.JavaPlugin;

public final class MemoryOptimizer implements Subsystem {
    private final JavaPlugin plugin;
    private ConfigSnapshot configSnapshot;

    public MemoryOptimizer(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "MemoryOptimizer";
    }

    @Override
    public void start() {
    }

    @Override
    public void stop() {
    }

    @Override
    public void applyConfig(ConfigSnapshot config) {
        this.configSnapshot = config;
    }

    @Override
    public void applyMode(Mode mode) {
        if (configSnapshot != null && configSnapshot.isDebug()) {
            plugin.getLogger().info("MemoryOptimizer mode applied: " + mode);
        }
    }
}
