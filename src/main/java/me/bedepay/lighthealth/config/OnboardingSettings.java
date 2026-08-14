package me.bedepay.lighthealth.config;

import org.bukkit.configuration.file.FileConfiguration;

public record OnboardingSettings(boolean enabled) {

    public static OnboardingSettings load(final FileConfiguration config) {
        return new OnboardingSettings(config.getBoolean("onboarding.enabled", true));
    }
}
