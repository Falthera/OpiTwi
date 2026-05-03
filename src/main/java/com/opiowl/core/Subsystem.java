package com.opiowl.core;

import com.opiowl.config.ConfigSnapshot;

public interface Subsystem {
    String getName();

    void start();

    void stop();

    void applyConfig(ConfigSnapshot config);

    void applyMode(Mode mode);
}
