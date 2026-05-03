package com.opiowl.config;

import com.opiowl.core.Mode;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

public final class ConfigSnapshot {
    public static final class Features {
        private final boolean tickEngine;
        private final boolean entityOptimizer;
        private final boolean chunkOptimizer;
        private final boolean networkOptimizer;
        private final boolean threadingScheduler;
        private final boolean memoryOptimizer;
        private final boolean perceivedLatency;
        private final boolean profiling;

        private Features(boolean tickEngine, boolean entityOptimizer, boolean chunkOptimizer, boolean networkOptimizer,
                         boolean threadingScheduler, boolean memoryOptimizer, boolean perceivedLatency, boolean profiling) {
            this.tickEngine = tickEngine;
            this.entityOptimizer = entityOptimizer;
            this.chunkOptimizer = chunkOptimizer;
            this.networkOptimizer = networkOptimizer;
            this.threadingScheduler = threadingScheduler;
            this.memoryOptimizer = memoryOptimizer;
            this.perceivedLatency = perceivedLatency;
            this.profiling = profiling;
        }

        public boolean tickEngine() {
            return tickEngine;
        }

        public boolean entityOptimizer() {
            return entityOptimizer;
        }

        public boolean chunkOptimizer() {
            return chunkOptimizer;
        }

        public boolean networkOptimizer() {
            return networkOptimizer;
        }

        public boolean threadingScheduler() {
            return threadingScheduler;
        }

        public boolean memoryOptimizer() {
            return memoryOptimizer;
        }

        public boolean perceivedLatency() {
            return perceivedLatency;
        }

        public boolean profiling() {
            return profiling;
        }
    }

    public static final class SafetyCaps {
        private final int maxEntityReductionPercent;
        private final int maxChunkThrottle;
        private final double maxTickSkip;

        private SafetyCaps(int maxEntityReductionPercent, int maxChunkThrottle, double maxTickSkip) {
            this.maxEntityReductionPercent = maxEntityReductionPercent;
            this.maxChunkThrottle = maxChunkThrottle;
            this.maxTickSkip = maxTickSkip;
        }

        public int maxEntityReductionPercent() {
            return maxEntityReductionPercent;
        }

        public int maxChunkThrottle() {
            return maxChunkThrottle;
        }

        public double maxTickSkip() {
            return maxTickSkip;
        }
    }

    public static final class Preset {
        private final int intensity;
        private final int maxEntityReductionPercent;
        private final int maxChunkThrottle;
        private final double maxTickSkip;

        private Preset(int intensity, int maxEntityReductionPercent, int maxChunkThrottle, double maxTickSkip) {
            this.intensity = intensity;
            this.maxEntityReductionPercent = maxEntityReductionPercent;
            this.maxChunkThrottle = maxChunkThrottle;
            this.maxTickSkip = maxTickSkip;
        }

        public int intensity() {
            return intensity;
        }

        public int maxEntityReductionPercent() {
            return maxEntityReductionPercent;
        }

        public int maxChunkThrottle() {
            return maxChunkThrottle;
        }

        public double maxTickSkip() {
            return maxTickSkip;
        }
    }

    public static final class EntitySettings {
        private final int sleepDistance;
        private final double mergeRadius;
        private final int mergeIntervalTicks;
        private final int maxMergePerTick;
        private final int aiThrottleDistance;

        private EntitySettings(int sleepDistance, double mergeRadius, int mergeIntervalTicks, int maxMergePerTick,
                               int aiThrottleDistance) {
            this.sleepDistance = sleepDistance;
            this.mergeRadius = mergeRadius;
            this.mergeIntervalTicks = mergeIntervalTicks;
            this.maxMergePerTick = maxMergePerTick;
            this.aiThrottleDistance = aiThrottleDistance;
        }

        public int sleepDistance() {
            return sleepDistance;
        }

        public double mergeRadius() {
            return mergeRadius;
        }

        public int mergeIntervalTicks() {
            return mergeIntervalTicks;
        }

        public int maxMergePerTick() {
            return maxMergePerTick;
        }

        public int aiThrottleDistance() {
            return aiThrottleDistance;
        }
    }

    public static final class TickSettings {
        private final int governorIntervalTicks;
        private final double priorityLaneBudgetMspt;
        private final int distanceNear;
        private final int distanceMid;
        private final int distanceFar;

        private TickSettings(int governorIntervalTicks, double priorityLaneBudgetMspt, int distanceNear, int distanceMid,
                             int distanceFar) {
            this.governorIntervalTicks = governorIntervalTicks;
            this.priorityLaneBudgetMspt = priorityLaneBudgetMspt;
            this.distanceNear = distanceNear;
            this.distanceMid = distanceMid;
            this.distanceFar = distanceFar;
        }

        public int governorIntervalTicks() {
            return governorIntervalTicks;
        }

        public double priorityLaneBudgetMspt() {
            return priorityLaneBudgetMspt;
        }

        public int distanceNear() {
            return distanceNear;
        }

        public int distanceMid() {
            return distanceMid;
        }

        public int distanceFar() {
            return distanceFar;
        }
    }

    public static final class ChunkSettings {
        private final int minViewDistance;
        private final int maxViewDistance;
        private final int adjustIntervalTicks;

        private ChunkSettings(int minViewDistance, int maxViewDistance, int adjustIntervalTicks) {
            this.minViewDistance = minViewDistance;
            this.maxViewDistance = maxViewDistance;
            this.adjustIntervalTicks = adjustIntervalTicks;
        }

        public int minViewDistance() {
            return minViewDistance;
        }

        public int maxViewDistance() {
            return maxViewDistance;
        }

        public int adjustIntervalTicks() {
            return adjustIntervalTicks;
        }
    }

    public static final class NetworkSettings {
        private final int batchIntervalTicks;
        private final int maxBatchSize;
        private final int smoothingWindowTicks;

        private NetworkSettings(int batchIntervalTicks, int maxBatchSize, int smoothingWindowTicks) {
            this.batchIntervalTicks = batchIntervalTicks;
            this.maxBatchSize = maxBatchSize;
            this.smoothingWindowTicks = smoothingWindowTicks;
        }

        public int batchIntervalTicks() {
            return batchIntervalTicks;
        }

        public int maxBatchSize() {
            return maxBatchSize;
        }

        public int smoothingWindowTicks() {
            return smoothingWindowTicks;
        }
    }

    public static final class ThreadingSettings {
        private final int maxTasksPerTick;
        private final int warnQueueSize;

        private ThreadingSettings(int maxTasksPerTick, int warnQueueSize) {
            this.maxTasksPerTick = maxTasksPerTick;
            this.warnQueueSize = warnQueueSize;
        }

        public int maxTasksPerTick() {
            return maxTasksPerTick;
        }

        public int warnQueueSize() {
            return warnQueueSize;
        }
    }

    public static final class ProfilingSettings {
        private final int sampleIntervalTicks;
        private final int historySize;
        private final boolean dashboardLogging;
        private final int dashboardIntervalTicks;

        private ProfilingSettings(int sampleIntervalTicks, int historySize, boolean dashboardLogging,
                                  int dashboardIntervalTicks) {
            this.sampleIntervalTicks = sampleIntervalTicks;
            this.historySize = historySize;
            this.dashboardLogging = dashboardLogging;
            this.dashboardIntervalTicks = dashboardIntervalTicks;
        }

        public int sampleIntervalTicks() {
            return sampleIntervalTicks;
        }

        public int historySize() {
            return historySize;
        }

        public boolean dashboardLogging() {
            return dashboardLogging;
        }

        public int dashboardIntervalTicks() {
            return dashboardIntervalTicks;
        }
    }

    private final int intensity;
    private final boolean debug;
    private final boolean autoMode;
    private final Mode configuredMode;
    private final Features features;
    private final SafetyCaps safetyCaps;
    private final Map<String, Preset> presets;
    private final EntitySettings entity;
    private final TickSettings tick;
    private final ChunkSettings chunks;
    private final NetworkSettings network;
    private final ThreadingSettings threading;
    private final ProfilingSettings profiling;

    private ConfigSnapshot(int intensity, boolean debug, boolean autoMode, Mode configuredMode, Features features,
                           SafetyCaps safetyCaps, Map<String, Preset> presets, EntitySettings entity, TickSettings tick,
                           ChunkSettings chunks, NetworkSettings network, ThreadingSettings threading,
                           ProfilingSettings profiling) {
        this.intensity = intensity;
        this.debug = debug;
        this.autoMode = autoMode;
        this.configuredMode = configuredMode;
        this.features = features;
        this.safetyCaps = safetyCaps;
        this.presets = presets;
        this.entity = entity;
        this.tick = tick;
        this.chunks = chunks;
        this.network = network;
        this.threading = threading;
        this.profiling = profiling;
    }

    public static ConfigSnapshot from(FileConfiguration config, Logger logger) {
        int intensity = ConfigValidator.clampInt(logger, "optimization.intensity",
            config.getInt("optimization.intensity", 60), 0, 100);
        boolean debug = config.getBoolean("optimization.debug", false);

        String modeRaw = config.getString("optimization.mode", "AUTO");
        boolean autoMode = true;
        Mode configuredMode = Mode.BALANCED;
        if (modeRaw != null) {
            String normalized = modeRaw.trim().toUpperCase(Locale.ROOT);
            if (!"AUTO".equals(normalized)) {
                try {
                    configuredMode = Mode.valueOf(normalized);
                    autoMode = false;
                } catch (IllegalArgumentException ex) {
                    logger.warning("Unknown optimization.mode: " + modeRaw + "; using AUTO");
                }
            }
        }

        Features features = new Features(
            config.getBoolean("features.tickEngine", true),
            config.getBoolean("features.entityOptimizer", true),
            config.getBoolean("features.chunkOptimizer", true),
            config.getBoolean("features.networkOptimizer", true),
            config.getBoolean("features.threadingScheduler", true),
            config.getBoolean("features.memoryOptimizer", true),
            config.getBoolean("features.perceivedLatency", true),
            config.getBoolean("features.profiling", true)
        );

        int maxEntityReductionPercent = ConfigValidator.clampInt(logger, "safetyCaps.maxEntityReductionPercent",
            config.getInt("safetyCaps.maxEntityReductionPercent", 60), 0, 100);
        int maxChunkThrottle = ConfigValidator.clampInt(logger, "safetyCaps.maxChunkThrottle",
            config.getInt("safetyCaps.maxChunkThrottle", 3), 0, 10);
        double maxTickSkip = ConfigValidator.clampDouble(logger, "safetyCaps.maxTickSkip",
            config.getDouble("safetyCaps.maxTickSkip", 0.2), 0.0, 0.5);
        SafetyCaps safetyCaps = new SafetyCaps(maxEntityReductionPercent, maxChunkThrottle, maxTickSkip);

        Map<String, Preset> presetMap = new HashMap<>();
        ConfigurationSection presetsSection = config.getConfigurationSection("presets");
        if (presetsSection != null) {
            for (String key : presetsSection.getKeys(false)) {
                ConfigurationSection presetSection = presetsSection.getConfigurationSection(key);
                if (presetSection == null) {
                    continue;
                }
                int presetIntensity = ConfigValidator.clampInt(logger, "presets." + key + ".intensity",
                    presetSection.getInt("intensity", intensity), 0, 100);
                int presetEntityReduction = ConfigValidator.clampInt(logger,
                    "presets." + key + ".maxEntityReductionPercent",
                    presetSection.getInt("maxEntityReductionPercent", maxEntityReductionPercent), 0, 100);
                int presetChunkThrottle = ConfigValidator.clampInt(logger,
                    "presets." + key + ".maxChunkThrottle",
                    presetSection.getInt("maxChunkThrottle", maxChunkThrottle), 0, 10);
                double presetTickSkip = ConfigValidator.clampDouble(logger, "presets." + key + ".maxTickSkip",
                    presetSection.getDouble("maxTickSkip", maxTickSkip), 0.0, 0.5);
                presetMap.put(key.toUpperCase(Locale.ROOT),
                    new Preset(presetIntensity, presetEntityReduction, presetChunkThrottle, presetTickSkip));
            }
        }

        int sleepDistance = ConfigValidator.clampInt(logger, "entity.sleepDistance",
            config.getInt("entity.sleepDistance", 48), 16, 128);
        double mergeRadius = ConfigValidator.clampDouble(logger, "entity.mergeRadius",
            config.getDouble("entity.mergeRadius", 1.75), 0.5, 8.0);
        int mergeIntervalTicks = ConfigValidator.clampInt(logger, "entity.mergeIntervalTicks",
            config.getInt("entity.mergeIntervalTicks", 40), 5, 200);
        int maxMergePerTick = ConfigValidator.clampInt(logger, "entity.maxMergePerTick",
            config.getInt("entity.maxMergePerTick", 300), 10, 2000);
        int aiThrottleDistance = ConfigValidator.clampInt(logger, "entity.aiThrottleDistance",
            config.getInt("entity.aiThrottleDistance", 32), 8, 96);
        EntitySettings entity = new EntitySettings(sleepDistance, mergeRadius, mergeIntervalTicks, maxMergePerTick,
            aiThrottleDistance);

        int governorIntervalTicks = ConfigValidator.clampInt(logger, "tick.governorIntervalTicks",
            config.getInt("tick.governorIntervalTicks", 20), 5, 200);
        double priorityLaneBudgetMspt = ConfigValidator.clampDouble(logger, "tick.priorityLaneBudgetMspt",
            config.getDouble("tick.priorityLaneBudgetMspt", 10.0), 1.0, 40.0);
        int distanceNear = ConfigValidator.clampInt(logger, "tick.distanceScale.near",
            config.getInt("tick.distanceScale.near", 16), 4, 48);
        int distanceMid = ConfigValidator.clampInt(logger, "tick.distanceScale.mid",
            config.getInt("tick.distanceScale.mid", 32), 8, 96);
        int distanceFar = ConfigValidator.clampInt(logger, "tick.distanceScale.far",
            config.getInt("tick.distanceScale.far", 64), 16, 128);
        TickSettings tick = new TickSettings(governorIntervalTicks, priorityLaneBudgetMspt, distanceNear, distanceMid,
            distanceFar);

        int minViewDistance = ConfigValidator.clampInt(logger, "chunks.minViewDistance",
            config.getInt("chunks.minViewDistance", 6), 2, 16);
        int maxViewDistance = ConfigValidator.clampInt(logger, "chunks.maxViewDistance",
            config.getInt("chunks.maxViewDistance", 12), 4, 32);
        if (minViewDistance > maxViewDistance) {
            int temp = minViewDistance;
            minViewDistance = maxViewDistance;
            maxViewDistance = temp;
            logger.warning("chunks.minViewDistance was greater than maxViewDistance; values swapped");
        }
        int adjustIntervalTicks = ConfigValidator.clampInt(logger, "chunks.adjustIntervalTicks",
            config.getInt("chunks.adjustIntervalTicks", 200), 20, 1200);
        ChunkSettings chunks = new ChunkSettings(minViewDistance, maxViewDistance, adjustIntervalTicks);

        int batchIntervalTicks = ConfigValidator.clampInt(logger, "network.batchIntervalTicks",
            config.getInt("network.batchIntervalTicks", 2), 1, 20);
        int maxBatchSize = ConfigValidator.clampInt(logger, "network.maxBatchSize",
            config.getInt("network.maxBatchSize", 64), 8, 512);
        int smoothingWindowTicks = ConfigValidator.clampInt(logger, "network.smoothingWindowTicks",
            config.getInt("network.smoothingWindowTicks", 4), 1, 20);
        NetworkSettings network = new NetworkSettings(batchIntervalTicks, maxBatchSize, smoothingWindowTicks);

        int maxTasksPerTick = ConfigValidator.clampInt(logger, "threading.maxTasksPerTick",
            config.getInt("threading.maxTasksPerTick", 200), 10, 10000);
        int warnQueueSize = ConfigValidator.clampInt(logger, "threading.warnQueueSize",
            config.getInt("threading.warnQueueSize", 5000), 100, 100000);
        ThreadingSettings threading = new ThreadingSettings(maxTasksPerTick, warnQueueSize);

        int sampleIntervalTicks = ConfigValidator.clampInt(logger, "profiling.sampleIntervalTicks",
            config.getInt("profiling.sampleIntervalTicks", 20), 5, 200);
        int historySize = ConfigValidator.clampInt(logger, "profiling.historySize",
            config.getInt("profiling.historySize", 300), 60, 3600);
        boolean dashboardLogging = config.getBoolean("profiling.dashboardLogging", false);
        int dashboardIntervalTicks = ConfigValidator.clampInt(logger, "profiling.dashboardIntervalTicks",
            config.getInt("profiling.dashboardIntervalTicks", 100), 20, 1200);
        ProfilingSettings profiling = new ProfilingSettings(sampleIntervalTicks, historySize, dashboardLogging,
            dashboardIntervalTicks);

        return new ConfigSnapshot(intensity, debug, autoMode, configuredMode, features, safetyCaps,
            Collections.unmodifiableMap(presetMap), entity, tick, chunks, network, threading, profiling);
    }

    public int getIntensity() {
        return intensity;
    }

    public boolean isDebug() {
        return debug;
    }

    public boolean isAutoMode() {
        return autoMode;
    }

    public Mode getConfiguredMode() {
        return configuredMode;
    }

    public Features features() {
        return features;
    }

    public SafetyCaps safetyCaps() {
        return safetyCaps;
    }

    public Map<String, Preset> presets() {
        return presets;
    }

    public EntitySettings entity() {
        return entity;
    }

    public TickSettings tick() {
        return tick;
    }

    public ChunkSettings chunks() {
        return chunks;
    }

    public NetworkSettings network() {
        return network;
    }

    public ThreadingSettings threading() {
        return threading;
    }

    public ProfilingSettings profiling() {
        return profiling;
    }
}
