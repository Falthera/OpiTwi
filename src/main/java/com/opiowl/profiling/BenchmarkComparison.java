package com.opiowl.profiling;

import java.util.List;

public final class BenchmarkComparison {
    private final LagReport vanilla;
    private final LagReport optimized;

    public BenchmarkComparison(List<MetricsSnapshot> vanillaSamples, List<MetricsSnapshot> optimizedSamples) {
        this.vanilla = LagReport.from(vanillaSamples);
        this.optimized = LagReport.from(optimizedSamples);
    }

    public LagReport getVanilla() {
        return vanilla;
    }

    public LagReport getOptimized() {
        return optimized;
    }

    public double getMsptImprovementPercent() {
        double vanillaMspt = vanilla.getAvgMspt();
        double optimizedMspt = optimized.getAvgMspt();
        if (vanillaMspt <= 0.0) {
            return 0.0;
        }
        return Math.max(0.0, ((vanillaMspt - optimizedMspt) / vanillaMspt) * 100.0);
    }
}
