package com.opiowl.core;

import com.opiowl.compat.ScheduledTask;
import com.opiowl.compat.SchedulerAdapter;
import com.opiowl.config.ConfigManager;
import com.opiowl.config.ConfigSnapshot;
import com.opiowl.profiling.MetricsSnapshot;
import com.opiowl.profiling.Profiler;
import com.opiowl.threading.TaskScheduler;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.plugin.java.JavaPlugin;

public final class OptimizationGovernor {
    private final JavaPlugin plugin;
    private final ConfigManager configManager;
    private final Profiler profiler;
    private final TaskScheduler taskScheduler;
    private final SchedulerAdapter schedulerAdapter;
    private final ServerMetrics serverMetrics;
    private final List<Subsystem> subsystems = new ArrayList<>();

    private ConfigSnapshot configSnapshot;
    private Mode currentMode = Mode.BALANCED;
    private ScheduledTask governorTask;
    private int governorIntervalTicks = 20;
    private boolean running;

    public OptimizationGovernor(JavaPlugin plugin, ConfigManager configManager, Profiler profiler, TaskScheduler taskScheduler,
                                SchedulerAdapter schedulerAdapter) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.profiler = profiler;
        this.taskScheduler = taskScheduler;
        this.schedulerAdapter = schedulerAdapter;
        this.serverMetrics = new ServerMetrics(plugin);
    }

    public void registerSubsystem(Subsystem subsystem) {
        subsystems.add(subsystem);
    }

    public void start() {
        ConfigSnapshot snapshot = configManager.getSnapshot();
        if (snapshot == null) {
            snapshot = configManager.reload();
        }
        applyConfig(snapshot);

        for (Subsystem subsystem : subsystems) {
            subsystem.start();
        }

        running = true;
        scheduleGovernorTask(governorIntervalTicks);
    }

    public void stop() {
        running = false;
        if (governorTask != null) {
            governorTask.cancel();
        }
        for (Subsystem subsystem : subsystems) {
            subsystem.stop();
        }
    }

    public void applyConfig(ConfigSnapshot snapshot) {
        if (snapshot == null) {
            return;
        }
        this.configSnapshot = snapshot;
        this.governorIntervalTicks = snapshot.tick().governorIntervalTicks();

        for (Subsystem subsystem : subsystems) {
            subsystem.applyConfig(snapshot);
        }

        taskScheduler.applyConfig(snapshot);
        scheduleGovernorTask(governorIntervalTicks);
        updateMode();
    }

    public Mode getCurrentMode() {
        return currentMode;
    }

    public ConfigSnapshot getConfigSnapshot() {
        return configSnapshot;
    }

    private void scheduleGovernorTask(int intervalTicks) {
        if (!running) {
            return;
        }
        if (governorTask != null) {
            governorTask.cancel();
        }
        governorTask = schedulerAdapter.runRepeating(this::updateLoop, intervalTicks, intervalTicks);
    }

    private void updateLoop() {
        if (!running) {
            return;
        }
        updateMode();
    }

    private void updateMode() {
        if (configSnapshot == null) {
            return;
        }

        Mode nextMode;
        if (configSnapshot.isAutoMode()) {
            MetricsSnapshot metrics = profiler.getLatestSnapshot();
            if (metrics == null) {
                metrics = serverMetrics.sample();
            }
            nextMode = decideMode(metrics);
        } else {
            nextMode = configSnapshot.getConfiguredMode();
        }

        if (nextMode != currentMode) {
            currentMode = nextMode;
            for (Subsystem subsystem : subsystems) {
                subsystem.applyMode(nextMode);
            }
            plugin.getLogger().info("Optimization mode set to " + nextMode);
        }
    }

    private Mode decideMode(MetricsSnapshot metrics) {
        double mspt = metrics.getMspt();
        double tps = metrics.getTps();

        if (mspt >= 55.0 || tps < 16.0) {
            return Mode.EMERGENCY_LAG_RECOVERY;
        }
        if (mspt >= 45.0 || tps < 18.0) {
            return Mode.ULTRA_OPTIMIZED;
        }
        if (mspt >= 35.0 || tps < 19.5) {
            return Mode.BALANCED;
        }
        return Mode.STABLE;
    }
}
