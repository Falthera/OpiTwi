package com.opiowl.api;

import com.opiowl.OpiOwlPlugin;
import com.opiowl.config.ConfigManager;
import com.opiowl.core.OptimizationGovernor;
import com.opiowl.profiling.Profiler;

public final class OpiOwlApi {
    private static volatile OpiOwlApi instance;

    private final OpiOwlPlugin plugin;
    private final OptimizationGovernor governor;
    private final Profiler profiler;
    private final ConfigManager configManager;

    private OpiOwlApi(OpiOwlPlugin plugin, OptimizationGovernor governor, Profiler profiler, ConfigManager configManager) {
        this.plugin = plugin;
        this.governor = governor;
        this.profiler = profiler;
        this.configManager = configManager;
    }

    public static void initialize(OpiOwlPlugin plugin, OptimizationGovernor governor, Profiler profiler, ConfigManager configManager) {
        instance = new OpiOwlApi(plugin, governor, profiler, configManager);
    }

    public static void shutdown() {
        instance = null;
    }

    public static OpiOwlApi get() {
        return instance;
    }

    public OpiOwlPlugin getPlugin() {
        return plugin;
    }

    public OptimizationGovernor getGovernor() {
        return governor;
    }

    public Profiler getProfiler() {
        return profiler;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }
}
