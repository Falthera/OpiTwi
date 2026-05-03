package com.opiowl.profiling;

public final class MetricsSnapshot {
    private final long timestamp;
    private final double tps;
    private final double mspt;
    private final int playerCount;
    private final int entityCount;
    private final int loadedChunkCount;
    private final int packetQueueSize;

    public MetricsSnapshot(long timestamp, double tps, double mspt, int playerCount, int entityCount,
                           int loadedChunkCount, int packetQueueSize) {
        this.timestamp = timestamp;
        this.tps = tps;
        this.mspt = mspt;
        this.playerCount = playerCount;
        this.entityCount = entityCount;
        this.loadedChunkCount = loadedChunkCount;
        this.packetQueueSize = packetQueueSize;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public double getTps() {
        return tps;
    }

    public double getMspt() {
        return mspt;
    }

    public int getPlayerCount() {
        return playerCount;
    }

    public int getEntityCount() {
        return entityCount;
    }

    public int getLoadedChunkCount() {
        return loadedChunkCount;
    }

    public int getPacketQueueSize() {
        return packetQueueSize;
    }
}
