package com.opiowl.compat;

public interface SchedulerAdapter {
    ScheduledTask runRepeating(Runnable task, long delayTicks, long periodTicks);

    ScheduledTask runLater(Runnable task, long delayTicks);

    boolean isFolia();
}
