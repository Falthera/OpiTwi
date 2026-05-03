package com.opiowl.profiling;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Instant;
import java.util.List;

public final class LagReport {
    private final long startTime;
    private final long endTime;
    private final double avgTps;
    private final double avgMspt;
    private final double maxMspt;
    private final int avgPlayers;
    private final int avgEntities;
    private final int avgChunks;

    private LagReport(long startTime, long endTime, double avgTps, double avgMspt, double maxMspt, int avgPlayers,
                      int avgEntities, int avgChunks) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.avgTps = avgTps;
        this.avgMspt = avgMspt;
        this.maxMspt = maxMspt;
        this.avgPlayers = avgPlayers;
        this.avgEntities = avgEntities;
        this.avgChunks = avgChunks;
    }

    public static LagReport from(List<MetricsSnapshot> snapshots) {
        if (snapshots.isEmpty()) {
            long now = System.currentTimeMillis();
            return new LagReport(now, now, 0.0, 0.0, 0.0, 0, 0, 0);
        }
        long start = snapshots.get(0).getTimestamp();
        long end = snapshots.get(snapshots.size() - 1).getTimestamp();

        double tpsSum = 0.0;
        double msptSum = 0.0;
        double maxMspt = 0.0;
        int playersSum = 0;
        int entitiesSum = 0;
        int chunksSum = 0;

        for (MetricsSnapshot snapshot : snapshots) {
            tpsSum += snapshot.getTps();
            msptSum += snapshot.getMspt();
            maxMspt = Math.max(maxMspt, snapshot.getMspt());
            playersSum += snapshot.getPlayerCount();
            entitiesSum += snapshot.getEntityCount();
            chunksSum += snapshot.getLoadedChunkCount();
        }

        int count = snapshots.size();
        return new LagReport(start, end,
            tpsSum / count,
            msptSum / count,
            maxMspt,
            playersSum / count,
            entitiesSum / count,
            chunksSum / count
        );
    }

    public String render() {
        StringBuilder builder = new StringBuilder();
        builder.append("OpiOwl Lag Report\n");
        builder.append("Generated: ").append(Instant.ofEpochMilli(endTime)).append("\n");
        builder.append("Window: ").append((endTime - startTime) / 1000).append("s\n");
        builder.append(String.format("Avg TPS: %.2f\n", avgTps));
        builder.append(String.format("Avg MSPT: %.2f\n", avgMspt));
        builder.append(String.format("Max MSPT: %.2f\n", maxMspt));
        builder.append("Avg Players: ").append(avgPlayers).append("\n");
        builder.append("Avg Entities: ").append(avgEntities).append("\n");
        builder.append("Avg Chunks: ").append(avgChunks).append("\n");
        return builder.toString();
    }

    public String summaryLine() {
        return String.format("TPS=%.2f MSPT=%.2f MaxMSPT=%.2f Players=%d Entities=%d Chunks=%d",
            avgTps, avgMspt, maxMspt, avgPlayers, avgEntities, avgChunks);
    }

    public double getAvgTps() {
        return avgTps;
    }

    public double getAvgMspt() {
        return avgMspt;
    }

    public double getMaxMspt() {
        return maxMspt;
    }

    public int getAvgPlayers() {
        return avgPlayers;
    }

    public int getAvgEntities() {
        return avgEntities;
    }

    public int getAvgChunks() {
        return avgChunks;
    }

    public File writeTo(File dataFolder) throws IOException {
        File reportsDir = new File(dataFolder, "reports");
        if (!reportsDir.exists() && !reportsDir.mkdirs()) {
            throw new IOException("Failed to create reports directory");
        }
        File reportFile = new File(reportsDir, "lagreport-" + endTime + ".txt");
        Files.writeString(reportFile.toPath(), render(), StandardCharsets.UTF_8);
        return reportFile;
    }
}
