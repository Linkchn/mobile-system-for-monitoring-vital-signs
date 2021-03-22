package com.grp.application;

public class HeartRateData {
    private long heartRate;
    private long timestamp;

    public HeartRateData(long hr, long timestamp) {
        this.heartRate = hr;
        this.timestamp = timestamp;
    }

    public long getHeartRate() {
        return heartRate;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
