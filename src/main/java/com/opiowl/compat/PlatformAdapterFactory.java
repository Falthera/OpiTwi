package com.opiowl.compat;

import java.lang.reflect.Method;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class PlatformAdapterFactory {
    private PlatformAdapterFactory() {
    }

    public static SchedulerAdapter create(JavaPlugin plugin) {
        if (isFoliaAvailable()) {
            try {
                return new FoliaSchedulerAdapter(plugin);
            } catch (Exception ex) {
                plugin.getLogger().warning("Folia scheduler unavailable, falling back to Bukkit: " + ex.getMessage());
            }
        }
        return new BukkitSchedulerAdapter(plugin);
    }

    public static boolean isFoliaAvailable() {
        try {
            Bukkit.class.getMethod("getGlobalRegionScheduler");
            return true;
        } catch (NoSuchMethodException ex) {
            return false;
        }
    }

    static Object resolveGlobalScheduler() throws ReflectiveOperationException {
        Method method = Bukkit.class.getMethod("getGlobalRegionScheduler");
        return method.invoke(null);
    }
}
