package com.opiowl.profiling;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class BenchmarkComparisonTest {
    @Test
    void reportsImprovementForLowerMspt() {
        List<MetricsSnapshot> vanilla = List.of(
            new MetricsSnapshot(1L, 18.0, 20.0, 10, 500, 50, 0),
            new MetricsSnapshot(2L, 17.0, 24.0, 10, 550, 52, 0)
        );
        List<MetricsSnapshot> optimized = List.of(
            new MetricsSnapshot(1L, 19.0, 12.0, 10, 500, 50, 0),
            new MetricsSnapshot(2L, 19.5, 11.0, 10, 520, 50, 0)
        );

        BenchmarkComparison comparison = new BenchmarkComparison(vanilla, optimized);

        assertTrue(comparison.getMsptImprovementPercent() > 0.0);
    }
}
