package com.opiowl.tick;

import com.opiowl.config.ConfigSnapshot;
import com.opiowl.core.Mode;
import com.opiowl.core.Subsystem;
import org.bukkit.plugin.java.JavaPlugin;

public final class TickEngine implements Subsystem {
    private final JavaPlugin plugin;
    private ConfigSnapshot configSnapshot;
    private Mode mode = Mode.BALANCED;

    public TickEngine(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "TickEngine";
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
        this.mode = mode;
        if (configSnapshot != null && configSnapshot.isDebug()) {
            plugin.getLogger().info("TickEngine mode applied: " + mode);
        }
    }
}
