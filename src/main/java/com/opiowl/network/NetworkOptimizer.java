package com.opiowl.network;

import com.opiowl.config.ConfigSnapshot;
import com.opiowl.core.Mode;
import com.opiowl.core.Subsystem;
import org.bukkit.plugin.java.JavaPlugin;

public final class NetworkOptimizer implements Subsystem {
    private final JavaPlugin plugin;
    private ConfigSnapshot configSnapshot;
    private Mode mode = Mode.BALANCED;

    public NetworkOptimizer(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "NetworkOptimizer";
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
    }

    public int getBatchIntervalTicks() {
        if (configSnapshot == null) {
            return 2;
        }
        int base = configSnapshot.network().batchIntervalTicks();
        switch (mode) {
            case ULTRA_OPTIMIZED:
                return Math.max(1, base / 2);
            case EMERGENCY_LAG_RECOVERY:
                return Math.max(1, base / 2);
            default:
                return base;
        }
    }

    public int getMaxBatchSize() {
        if (configSnapshot == null) {
            return 64;
        }
        int base = configSnapshot.network().maxBatchSize();
        switch (mode) {
            case ULTRA_OPTIMIZED:
                return Math.min(base * 2, 512);
            case EMERGENCY_LAG_RECOVERY:
                return Math.min(base * 3, 512);
            default:
                return base;
        }
    }
}
