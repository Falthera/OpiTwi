package com.opiowl.chunks;

import com.opiowl.compat.ScheduledTask;
import com.opiowl.compat.SchedulerAdapter;
import com.opiowl.config.ConfigSnapshot;
import com.opiowl.core.Mode;
import com.opiowl.core.Subsystem;
import java.lang.reflect.Method;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class ChunkOptimizer implements Subsystem {
    private final JavaPlugin plugin;
    private final SchedulerAdapter schedulerAdapter;

    private ConfigSnapshot configSnapshot;
    private ScheduledTask adjustTask;
    private Mode mode = Mode.BALANCED;
    private int currentViewDistance = -1;

    public ChunkOptimizer(JavaPlugin plugin, SchedulerAdapter schedulerAdapter) {
        this.plugin = plugin;
        this.schedulerAdapter = schedulerAdapter;
    }

    @Override
    public String getName() {
        return "ChunkOptimizer";
    }

    @Override
    public void start() {
        scheduleAdjustTask();
    }

    @Override
    public void stop() {
        if (adjustTask != null) {
            adjustTask.cancel();
        }
    }

    @Override
    public void applyConfig(ConfigSnapshot config) {
        this.configSnapshot = config;
        scheduleAdjustTask();
        updateViewDistance();
    }

    @Override
    public void applyMode(Mode mode) {
        this.mode = mode;
        updateViewDistance();
    }

    private void scheduleAdjustTask() {
        if (adjustTask != null) {
            adjustTask.cancel();
        }
        if (configSnapshot == null || !configSnapshot.features().chunkOptimizer()) {
            return;
        }
        int interval = configSnapshot.chunks().adjustIntervalTicks();
        adjustTask = schedulerAdapter.runRepeating(this::updateViewDistance, interval, interval);
    }

    private void updateViewDistance() {
        if (configSnapshot == null || !configSnapshot.features().chunkOptimizer()) {
            return;
        }
        int min = configSnapshot.chunks().minViewDistance();
        int max = configSnapshot.chunks().maxViewDistance();
        int target = calculateTargetViewDistance(min, max);
        if (target == currentViewDistance) {
            return;
        }
        try {
            Method method = Bukkit.getServer().getClass().getMethod("setViewDistance", int.class);
            method.invoke(Bukkit.getServer(), target);
            currentViewDistance = target;
            if (configSnapshot.isDebug()) {
                plugin.getLogger().info("Chunk view distance set to " + target);
            }
        } catch (Throwable ex) {
            plugin.getLogger().warning("Failed to set view distance: " + ex.getMessage());
        }
    }

    private int calculateTargetViewDistance(int min, int max) {
        switch (mode) {
            case STABLE:
                return max;
            case BALANCED:
                return Math.max(min, max - 1);
            case ULTRA_OPTIMIZED:
                return Math.max(min, max - 2);
            case EMERGENCY_LAG_RECOVERY:
                return min;
            default:
                return max;
        }
    }
}
