package com.opiowl.profiling;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class LagReportTest {
    @Test
    void rendersSummaryForSnapshots() {
        List<MetricsSnapshot> snapshots = List.of(
            new MetricsSnapshot(1L, 20.0, 10.0, 5, 100, 20, 0),
            new MetricsSnapshot(2L, 18.0, 15.0, 6, 120, 24, 0)
        );

        LagReport report = LagReport.from(snapshots);

        assertTrue(report.summaryLine().contains("TPS=19.00"));
        assertTrue(report.summaryLine().contains("MSPT=12.50"));
    }
}
