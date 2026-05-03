package com.opiowl.profiling;

import com.opiowl.compat.ScheduledTask;
import com.opiowl.compat.SchedulerAdapter;
import com.opiowl.config.ConfigManager;
import com.opiowl.config.ConfigSnapshot;
import com.opiowl.core.ServerMetrics;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import org.bukkit.plugin.java.JavaPlugin;

public final class Profiler {
    private final JavaPlugin plugin;
    private final SchedulerAdapter schedulerAdapter;
    private final ConfigManager configManager;
    private final ServerMetrics serverMetrics;
    private final Deque<MetricsSnapshot> history = new ArrayDeque<>();

    private ConfigSnapshot configSnapshot;
    private ScheduledTask sampleTask;
    private ScheduledTask dashboardTask;
    private boolean running;

    public Profiler(JavaPlugin plugin, SchedulerAdapter schedulerAdapter, ConfigManager configManager) {
        this.plugin = plugin;
        this.schedulerAdapter = schedulerAdapter;
        this.configManager = configManager;
        this.serverMetrics = new ServerMetrics(plugin);
    }

    public void start() {
        running = true;
        applyConfig(configManager.getSnapshot());
    }

    public void stop() {
        running = false;
        if (sampleTask != null) {
            sampleTask.cancel();
        }
        if (dashboardTask != null) {
            dashboardTask.cancel();
        }
        history.clear();
    }

    public void applyConfig(ConfigSnapshot snapshot) {
        if (snapshot == null) {
            return;
        }
        this.configSnapshot = snapshot;
        rescheduleSampling(snapshot.profiling().sampleIntervalTicks());
        rescheduleDashboard(snapshot.profiling().dashboardLogging(), snapshot.profiling().dashboardIntervalTicks());
    }

    public MetricsSnapshot getLatestSnapshot() {
        return history.peekLast();
    }

    public List<MetricsSnapshot> getHistorySnapshot() {
        return new ArrayList<>(history);
    }

    public LagReport buildReport() {
        return LagReport.from(getHistorySnapshot());
    }

    private void rescheduleSampling(int intervalTicks) {
        if (sampleTask != null) {
            sampleTask.cancel();
        }
        sampleTask = schedulerAdapter.runRepeating(this::sample, intervalTicks, intervalTicks);
    }

    private void rescheduleDashboard(boolean enabled, int intervalTicks) {
        if (dashboardTask != null) {
            dashboardTask.cancel();
        }
        if (!enabled) {
            return;
        }
        dashboardTask = schedulerAdapter.runRepeating(this::logDashboard, intervalTicks, intervalTicks);
    }

    private void sample() {
        if (!running || configSnapshot == null) {
            return;
        }
        MetricsSnapshot snapshot = serverMetrics.sample();
        history.addLast(snapshot);
        while (history.size() > configSnapshot.profiling().historySize()) {
            history.removeFirst();
        }
    }

    private void logDashboard() {
        MetricsSnapshot snapshot = getLatestSnapshot();
        if (snapshot == null) {
            return;
        }
        plugin.getLogger().info(String.format(
            "Dashboard TPS=%.2f MSPT=%.2f Players=%d Entities=%d Chunks=%d",
            snapshot.getTps(), snapshot.getMspt(), snapshot.getPlayerCount(), snapshot.getEntityCount(),
            snapshot.getLoadedChunkCount()
        ));
    }
}
