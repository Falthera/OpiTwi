package com.opiowl.config;

import java.util.logging.Logger;

public final class ConfigValidator {
    private ConfigValidator() {
    }

    public static int clampInt(Logger logger, String path, int value, int min, int max) {
        int clamped = Math.max(min, Math.min(max, value));
        if (clamped != value) {
            logger.warning(path + " clamped from " + value + " to " + clamped);
        }
        return clamped;
    }

    public static double clampDouble(Logger logger, String path, double value, double min, double max) {
        double clamped = Math.max(min, Math.min(max, value));
        if (Double.compare(clamped, value) != 0) {
            logger.warning(path + " clamped from " + value + " to " + clamped);
        }
        return clamped;
    }
}
