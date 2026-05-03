package com.opiowl.profiling;

import java.util.ArrayList;
import java.util.List;

public final class StressHarness {
    public List<MetricsSnapshot> simulateEntityStorm(int basePlayers, int entityCount, int chunkCount, int samples) {
        List<MetricsSnapshot> snapshots = new ArrayList<>();
        for (int index = 0; index < samples; index++) {
            double pressure = 1.0 + (entityCount / 500.0) + (chunkCount / 200.0) + (index / 25.0);
            double tps = Math.max(1.0, 20.0 - pressure * 2.25);
            double mspt = 5.0 + pressure * 6.5;
            snapshots.add(new MetricsSnapshot(System.currentTimeMillis(), tps, mspt, basePlayers, entityCount,
                chunkCount, -1));
        }
        return snapshots;
    }

    public List<MetricsSnapshot> simulateChunkLoadStorm(int basePlayers, int entityCount, int chunkCount, int samples) {
        List<MetricsSnapshot> snapshots = new ArrayList<>();
        for (int index = 0; index < samples; index++) {
            double pressure = 1.0 + (chunkCount / 100.0) + (index / 20.0);
            double tps = Math.max(1.0, 20.0 - pressure * 1.75);
            double mspt = 4.0 + pressure * 5.0;
            snapshots.add(new MetricsSnapshot(System.currentTimeMillis(), tps, mspt, basePlayers, entityCount,
                chunkCount, -1));
        }
        return snapshots;
    }
}
