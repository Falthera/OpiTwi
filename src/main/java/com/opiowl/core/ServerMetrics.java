package com.opiowl.core;

import com.opiowl.profiling.MetricsSnapshot;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Method;

public final class ServerMetrics {
    private final JavaPlugin plugin;

    public ServerMetrics(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public MetricsSnapshot sample() {
        double tps = sampleServerDouble("getTPS", 20.0);
        double mspt = sampleServerDouble("getAverageTickTime", 0.0);

        int playerCount = Bukkit.getOnlinePlayers().size();
        int entityCount = 0;
        int loadedChunks = 0;
        for (World world : Bukkit.getWorlds()) {
            entityCount += world.getEntities().size();
            loadedChunks += world.getLoadedChunks().length;
        }

        return new MetricsSnapshot(System.currentTimeMillis(), tps, mspt, playerCount, entityCount, loadedChunks, -1);
    }

    private double sampleServerDouble(String methodName, double fallback) {
        try {
            Method method = Bukkit.getServer().getClass().getMethod(methodName);
            Object value = method.invoke(Bukkit.getServer());
            if (value instanceof double[]) {
                double[] values = (double[]) value;
                if (values.length > 0) {
                    return values[0];
                }
            }
            if (value instanceof Number) {
                return ((Number) value).doubleValue();
            }
        } catch (ReflectiveOperationException ignored) {
            plugin.getLogger().fine(methodName + " sampling not available.");
        }
        return fallback;
    }
}
