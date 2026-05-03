package com.opiowl.threading;

import com.opiowl.compat.ScheduledTask;
import com.opiowl.compat.SchedulerAdapter;
import com.opiowl.config.ConfigSnapshot;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.bukkit.plugin.java.JavaPlugin;

public final class TaskScheduler {
    private final JavaPlugin plugin;
    private final SchedulerAdapter schedulerAdapter;
    private final ConcurrentLinkedQueue<Runnable> highQueue = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<Runnable> mediumQueue = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<Runnable> lowQueue = new ConcurrentLinkedQueue<>();

    private ScheduledTask drainTask;
    private volatile int maxTasksPerTick = 200;
    private volatile int warnQueueSize = 5000;
    private long lastWarnTime;

    public TaskScheduler(JavaPlugin plugin, SchedulerAdapter schedulerAdapter) {
        this.plugin = plugin;
        this.schedulerAdapter = schedulerAdapter;
        this.drainTask = schedulerAdapter.runRepeating(this::drainQueues, 1, 1);
    }

    public void submit(TaskPriority priority, Runnable task) {
        if (task == null) {
            return;
        }
        switch (priority) {
            case HIGH:
                highQueue.add(task);
                break;
            case MEDIUM:
                mediumQueue.add(task);
                break;
            case LOW:
            default:
                lowQueue.add(task);
                break;
        }
    }

    public void applyConfig(ConfigSnapshot snapshot) {
        if (snapshot == null) {
            return;
        }
        this.maxTasksPerTick = snapshot.threading().maxTasksPerTick();
        this.warnQueueSize = snapshot.threading().warnQueueSize();
    }

    private void drainQueues() {
        int budget = maxTasksPerTick;
        budget = drainQueue(highQueue, budget);
        if (budget > 0) {
            budget = drainQueue(mediumQueue, budget);
        }
        if (budget > 0) {
            drainQueue(lowQueue, budget);
        }
        warnIfQueuesLarge();
    }

    private int drainQueue(ConcurrentLinkedQueue<Runnable> queue, int budget) {
        while (budget > 0) {
            Runnable task = queue.poll();
            if (task == null) {
                break;
            }
            try {
                task.run();
            } catch (Exception ex) {
                plugin.getLogger().warning("TaskScheduler task failed: " + ex.getMessage());
            }
            budget--;
        }
        return budget;
    }

    private void warnIfQueuesLarge() {
        int queueSize = highQueue.size() + mediumQueue.size() + lowQueue.size();
        long now = System.currentTimeMillis();
        if (queueSize > warnQueueSize && now - lastWarnTime > 10000) {
            lastWarnTime = now;
            plugin.getLogger().warning("TaskScheduler backlog: " + queueSize + " tasks");
        }
    }
}
