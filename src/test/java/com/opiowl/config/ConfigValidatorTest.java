package com.opiowl.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.logging.Logger;
import org.junit.jupiter.api.Test;

class ConfigValidatorTest {
    @Test
    void clampsIntegersAndDoubles() {
        Logger logger = Logger.getAnonymousLogger();

        assertEquals(10, ConfigValidator.clampInt(logger, "value", 20, 0, 10));
        assertEquals(0.5, ConfigValidator.clampDouble(logger, "ratio", 1.5, 0.0, 0.5), 0.0001);
    }
}
