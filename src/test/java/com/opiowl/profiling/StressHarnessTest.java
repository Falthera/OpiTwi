package com.opiowl.profiling;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.List;
import org.junit.jupiter.api.Test;

class StressHarnessTest {
    @Test
    void simulatesEntityStormSamples() {
        StressHarness harness = new StressHarness();
        List<MetricsSnapshot> snapshots = harness.simulateEntityStorm(20, 500, 40, 5);

        assertEquals(5, snapshots.size());
        assertFalse(snapshots.isEmpty());
    }
}
