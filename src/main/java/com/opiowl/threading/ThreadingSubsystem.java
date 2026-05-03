package com.opiowl.threading;

import com.opiowl.config.ConfigSnapshot;
import com.opiowl.core.Mode;
import com.opiowl.core.Subsystem;

public final class ThreadingSubsystem implements Subsystem {
    private final TaskScheduler taskScheduler;

    public ThreadingSubsystem(TaskScheduler taskScheduler) {
        this.taskScheduler = taskScheduler;
    }

    @Override
    public String getName() {
        return "ThreadingScheduler";
    }

    @Override
    public void start() {
    }

    @Override
    public void stop() {
    }

    @Override
    public void applyConfig(ConfigSnapshot config) {
        taskScheduler.applyConfig(config);
    }

    @Override
    public void applyMode(Mode mode) {
    }
}
