package me.bedepay.lighthealth.config;

import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OnboardingSettingsTest {

    @Test
    void defaultsToEnabled() {
        assertTrue(OnboardingSettings.load(new YamlConfiguration()).enabled());
    }

    @Test
    void readsDisabledSetting() {
        final YamlConfiguration config = new YamlConfiguration();
        config.set("onboarding.enabled", false);

        assertFalse(OnboardingSettings.load(config).enabled());
    }
}
