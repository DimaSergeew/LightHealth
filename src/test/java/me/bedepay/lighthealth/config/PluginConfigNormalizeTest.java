package me.bedepay.lighthealth.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PluginConfigNormalizeTest {

    @Test
    void keepsOrderedThresholds() {
        assertArrayEquals(new double[] {50.0, 25.0}, PluginConfig.normalizeThresholds(null, 50.0, 25.0));
    }

    @Test
    void swapsInvertedThresholds() {
        assertArrayEquals(new double[] {50.0, 20.0}, PluginConfig.normalizeThresholds(null, 20.0, 50.0));
    }

    @Test
    void clampsOutOfRangeThresholds() {
        final double[] values = PluginConfig.normalizeThresholds(null, 140.0, -10.0);
        assertEquals(100.0, values[0]);
        assertEquals(0.0, values[1]);
    }
}
