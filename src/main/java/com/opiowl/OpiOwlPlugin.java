package com.opiowl;

import com.opiowl.api.OpiOwlApi;
import com.opiowl.chunks.ChunkOptimizer;
import com.opiowl.commands.OpiOwlCommand;
import com.opiowl.compat.PlatformAdapterFactory;
import com.opiowl.compat.SchedulerAdapter;
import com.opiowl.config.ConfigManager;
import com.opiowl.core.OptimizationGovernor;
import com.opiowl.core.Subsystem;
import com.opiowl.entity.EntityOptimizer;
import com.opiowl.memory.MemoryOptimizer;
import com.opiowl.network.NetworkOptimizer;
import com.opiowl.profiling.Profiler;
import com.opiowl.threading.TaskScheduler;
import com.opiowl.threading.ThreadingSubsystem;
import com.opiowl.tick.TickEngine;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class OpiOwlPlugin extends JavaPlugin {
    private ConfigManager configManager;
    private SchedulerAdapter schedulerAdapter;
    private TaskScheduler taskScheduler;
    private Profiler profiler;
    private OptimizationGovernor governor;
    private final List<Subsystem> subsystems = new ArrayList<>();

    @Override
    public void onEnable() {
        saveDefaultConfig();

        configManager = new ConfigManager(this);
        configManager.reload();

        schedulerAdapter = PlatformAdapterFactory.create(this);
        taskScheduler = new TaskScheduler(this, schedulerAdapter);

        profiler = new Profiler(this, schedulerAdapter, configManager);
        governor = new OptimizationGovernor(this, configManager, profiler, taskScheduler, schedulerAdapter);

        registerSubsystem(new TickEngine(this));
        registerSubsystem(new EntityOptimizer(this, schedulerAdapter));
        registerSubsystem(new ChunkOptimizer(this, schedulerAdapter));
        registerSubsystem(new NetworkOptimizer(this));
        registerSubsystem(new MemoryOptimizer(this));
        registerSubsystem(new ThreadingSubsystem(taskScheduler));

        for (Subsystem subsystem : subsystems) {
            governor.registerSubsystem(subsystem);
        }

        governor.start();
        profiler.start();

        OpiOwlApi.initialize(this, governor, profiler, configManager);

        registerCommands();
        getLogger().info("OpiOwl enabled.");
    }

    @Override
    public void onDisable() {
        if (governor != null) {
            governor.stop();
        }
        if (profiler != null) {
            profiler.stop();
        }
        OpiOwlApi.shutdown();
        getLogger().info("OpiOwl disabled.");
    }

    private void registerSubsystem(Subsystem subsystem) {
        subsystems.add(subsystem);
    }

    private void registerCommands() {
        PluginCommand command = getCommand("opiowl");
        if (command == null) {
            getLogger().warning("Command opiowl is not registered in plugin.yml.");
            return;
        }
        OpiOwlCommand handler = new OpiOwlCommand(this, governor, profiler, configManager);
        command.setExecutor(handler);
        command.setTabCompleter(handler);
    }
}
