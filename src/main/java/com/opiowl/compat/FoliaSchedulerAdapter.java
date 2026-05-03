package com.opiowl.compat;

import java.lang.reflect.Method;
import java.util.function.Consumer;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class FoliaSchedulerAdapter implements SchedulerAdapter {
    private final JavaPlugin plugin;
    private final Object globalScheduler;
    private final Method runAtFixedRate;
    private final Method runDelayed;

    public FoliaSchedulerAdapter(JavaPlugin plugin) throws ReflectiveOperationException {
        this.plugin = plugin;
        this.globalScheduler = PlatformAdapterFactory.resolveGlobalScheduler();
        this.runAtFixedRate = findMethod(globalScheduler.getClass(), "runAtFixedRate", 4);
        this.runDelayed = findMethod(globalScheduler.getClass(), "runDelayed", 3);
    }

    @Override
    public ScheduledTask runRepeating(Runnable task, long delayTicks, long periodTicks) {
        try {
            Object handle = runAtFixedRate.invoke(globalScheduler, plugin, asConsumer(task), delayTicks, periodTicks);
            return wrapHandle(handle);
        } catch (Exception ex) {
            Bukkit.getLogger().warning("Folia scheduler runRepeating failed: " + ex.getMessage());
            return () -> {
            };
        }
    }

    @Override
    public ScheduledTask runLater(Runnable task, long delayTicks) {
        try {
            Object handle = runDelayed.invoke(globalScheduler, plugin, asConsumer(task), delayTicks);
            return wrapHandle(handle);
        } catch (Exception ex) {
            Bukkit.getLogger().warning("Folia scheduler runLater failed: " + ex.getMessage());
            return () -> {
            };
        }
    }

    @Override
    public boolean isFolia() {
        return true;
    }

    private Consumer<Object> asConsumer(Runnable task) {
        return scheduledTask -> task.run();
    }

    private ScheduledTask wrapHandle(Object handle) {
        if (handle == null) {
            return () -> {
            };
        }
        try {
            Method cancelMethod = handle.getClass().getMethod("cancel");
            return () -> {
                try {
                    cancelMethod.invoke(handle);
                } catch (Exception ignored) {
                }
            };
        } catch (Exception ex) {
            return () -> {
            };
        }
    }

    private Method findMethod(Class<?> type, String name, int paramCount) throws NoSuchMethodException {
        for (Method method : type.getMethods()) {
            if (method.getName().equals(name) && method.getParameterCount() == paramCount) {
                return method;
            }
        }
        throw new NoSuchMethodException(name);
    }
}
